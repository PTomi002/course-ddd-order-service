package hu.paulintamas.foodorderingsystem.service.domain;

import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderStatus;
import hu.paulintamas.foodorderingsystem.domain.valueobject.PaymentStatus;
import hu.paulintamas.foodorderingsystem.outbox.OutboxStatus;
import hu.paulintamas.foodorderingsystem.saga.SagaStatus;
import hu.paulintamas.foodorderingsystem.saga.SagaStep;
import hu.paulintamas.foodorderingsystem.service.domain.dto.message.PaymentResponse;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Order;
import hu.paulintamas.foodorderingsystem.service.domain.event.OrderPaidEvent;
import hu.paulintamas.foodorderingsystem.service.domain.exception.OrderDomainException;
import hu.paulintamas.foodorderingsystem.service.domain.mapper.OrderDataMapper;
import hu.paulintamas.foodorderingsystem.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import hu.paulintamas.foodorderingsystem.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import hu.paulintamas.foodorderingsystem.service.domain.outbox.scheduler.approval.ApprovalOutboxHelper;
import hu.paulintamas.foodorderingsystem.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * [KNOWLEDGE-SAGA] Saga Coordinator/Orchestrator class.
 * Flow:
 * - Payment is PAID or the saga flow stops with an empty event.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaymentSaga implements SagaStep<PaymentResponse> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final OrderDataMapper orderDataMapper;

    /*
        [KNOWLEDGE-OUTBOX]
        We dont need them as Outbox Table + Scheduler will handle the event publishing.
        private final OrderPaidRestaurantMessagePublisher orderPaidRestaurantMessagePublisher;
    */
    @Override
    @Transactional // the purpose of Outbox pattern is to run all DB transactions in one ACID transaction
    public void process(PaymentResponse paymentResponse) {
        // If the database has the READ_COMMITTED: second thread will wait for the first tx to be committed
        // If the database has the UNCOMMITTED_READ: second thread will not find any data
        var outboxMessageResponse = paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(paymentResponse.getSagaId()),
                SagaStatus.STARTED
        );
        if (outboxMessageResponse.isEmpty()) {
            // If an outbox message is processed twice on the kafka topic: ([KNOWLEDGE] Idempotent Consumer)
            // - the first message will be processed and be updated
            // - the second duplicate message will not have SagaStatus.STARTED
            // Will work when multiple instances are running because:
            //  - more app instances within the same consumer group wont pick up the same message
            //  - duplication will be sent to the same partition because of the same key which hashed by kafka
            log.info("An outbox message with saga id: {} is already processed for payment!", paymentResponse.getSagaId());
            return;
        }

        log.info("Completing payment for order id: {}", paymentResponse.getOrderId());
        var orderPaidDomainEvent = completePaymentForOrder(paymentResponse);
        var outboxMessage = outboxMessageResponse.get();
        var sagaStatus = orderSagaHelper.orderStatusToSagaStatus(orderPaidDomainEvent.getOrder().getOrderStatus());

/*
        If we consume the kafka records (after fetch) in a parallel way, there is a chance that the duplicate records will be consumed at the same time.
        To prevent collisions we have some kind of locking:
            - optimistic locking: we expect only a few collisions so this is better than pessimistic locking
            - pessimistic locking: we should use it if we expect a lot of collision, bad for performance in our case
 */
        paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(outboxMessage, orderPaidDomainEvent.getOrder().getOrderStatus(), sagaStatus));
        // restaurant_approval_outbox_saga_id index is unique so the second thread would get an exception for trying to insert
        approvalOutboxHelper.saveApprovalOutboxMessage(
                orderDataMapper.orderPaidEventToOrderApprovalEventPayload(orderPaidDomainEvent),
                orderPaidDomainEvent.getOrder().getOrderStatus(),
                sagaStatus,
                OutboxStatus.STARTED,
                UUID.fromString(paymentResponse.getSagaId())
        );
        log.info("Order with id: {} is paid", orderPaidDomainEvent.getOrder().getId().getValue());
    }

    @Override
    @Transactional
    public void rollback(PaymentResponse paymentResponse) {
        var outboxMessageResponse = paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(paymentResponse.getSagaId()),
                getCurrentSagaStatus(paymentResponse)
        );
        if (outboxMessageResponse.isEmpty()) {
            log.info("An outbox message with saga id: {} is already rolled back for payment!", paymentResponse.getSagaId());
            return;
        }

        log.info("Cancelling order with id: {}", paymentResponse.getOrderId());
        var outboxMessage = outboxMessageResponse.get();
        var order = completeCancelForOrder(paymentResponse);
        var sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());

        paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(outboxMessage, order.getOrderStatus(), sagaStatus));
        if (paymentResponse.getPaymentStatus() == PaymentStatus.CANCELLED) {
            approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(paymentResponse.getSagaId(), order.getOrderStatus(), sagaStatus));
        }

        log.info("Order with id: {} is cancelled", order.getId().getValue());
    }

    private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(
            String sagaId,
            OrderStatus orderStatus,
            SagaStatus sagaStatus
    ) {
        var outboxResponse = approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(sagaId),
                SagaStatus.COMPENSATING
        );
        if (outboxResponse.isEmpty()) {
            log.error("Approval outbox message could not be found in: " + SagaStatus.COMPENSATING.name() + " status!");
            throw new OrderDomainException("Approval outbox message could not be found in: " + SagaStatus.COMPENSATING.name() + " status!");
        }
        var outboxMessage = outboxResponse.get();
        outboxMessage.setProcessedAt(ZonedDateTime.now());
        outboxMessage.setOrderStatus(orderStatus);
        outboxMessage.setSagaStatus(sagaStatus);
        return outboxMessage;
    }

    private SagaStatus[] getCurrentSagaStatus(PaymentResponse paymentResponse) {
        return switch (paymentResponse.getPaymentStatus()) {
            case COMPLETED -> new SagaStatus[]{SagaStatus.STARTED};
            case CANCELLED -> new SagaStatus[]{SagaStatus.PROCESSING};
            case FAILED -> new SagaStatus[]{SagaStatus.STARTED, SagaStatus.PROCESSING};
        };
    }

    private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(
            OrderPaymentOutboxMessage outboxMessage,
            OrderStatus orderStatus,
            SagaStatus sagaStatus
    ) {
        outboxMessage.setProcessedAt(ZonedDateTime.now());
        outboxMessage.setOrderStatus(orderStatus);
        outboxMessage.setSagaStatus(sagaStatus);
        return outboxMessage;
    }

    private OrderPaidEvent completePaymentForOrder(PaymentResponse paymentResponse) {
        var order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        var domainEvent = orderDomainService.payOrder(order);
        orderSagaHelper.save(order);
        return domainEvent;
    }

    private Order completeCancelForOrder(PaymentResponse paymentResponse) {
        var order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        orderDomainService.cancelOrder(order, paymentResponse.getFailureMessages());
        orderSagaHelper.save(order);
        return order;
    }

}
