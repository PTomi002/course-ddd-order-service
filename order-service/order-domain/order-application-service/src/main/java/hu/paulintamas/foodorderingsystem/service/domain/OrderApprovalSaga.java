package hu.paulintamas.foodorderingsystem.service.domain;

import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderStatus;
import hu.paulintamas.foodorderingsystem.outbox.OutboxStatus;
import hu.paulintamas.foodorderingsystem.saga.SagaStatus;
import hu.paulintamas.foodorderingsystem.saga.SagaStep;
import hu.paulintamas.foodorderingsystem.service.domain.dto.message.RestaurantApprovalResponse;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Order;
import hu.paulintamas.foodorderingsystem.service.domain.event.OrderCancelledEvent;
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
 *  - Order is also APPROVED from the restaurant side -> the flow is finished, or rollback with cancel event.
 * Impl. Details:
 *  - @Transactional -> Want the DB to finish successfully first to be consistent with the eventing out.
 *  - BUT even now the publishing can fail and leaving the system in inconsistent state.
 *      What we con do now?
 *          (1) Retry for message subsystem producer and consumer to handle mostly network errors and use the idempotentKey for consumers.
 *          (2) [KNOWLEDGE-OUTBOX] Adding Outbox pattern next to Saga pattern.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse> {

    private final OrderSagaHelper orderSagaHelper;
    private final OrderDomainService orderDomainService;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final OrderDataMapper orderDataMapper;

    @Override
    @Transactional
    public void process(RestaurantApprovalResponse restaurantApprovalResponse) {
        var outboxMessageResponse = approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(restaurantApprovalResponse.getSagaId()),
                SagaStatus.PROCESSING
        );
        if (outboxMessageResponse.isEmpty()) {
            log.info("An outbox message with saga id: {} is already processed for approval!", restaurantApprovalResponse.getSagaId());
            return;
        }

        log.info("Approving order with id: {}", restaurantApprovalResponse.getOrderId());
        var outboxMessage = outboxMessageResponse.get();
        var order = completeApprovalForOrder(restaurantApprovalResponse);
        var sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());

        approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(outboxMessage, order.getOrderStatus(), sagaStatus));
        paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(restaurantApprovalResponse.getSagaId(), order.getOrderStatus(), sagaStatus));

        log.info("Order with id: {} is approved", order.getId().getValue());
    }

    @Override
    @Transactional
    public void rollback(RestaurantApprovalResponse restaurantApprovalResponse) {
        var outboxMessageResponse = approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(restaurantApprovalResponse.getSagaId()),
                SagaStatus.PROCESSING
        );
        if (outboxMessageResponse.isEmpty()) {
            log.info("An outbox message with saga id: {} is already rolled back for approval!", restaurantApprovalResponse.getSagaId());
            return;
        }

        log.info("Cancelling order with id: {}", restaurantApprovalResponse.getOrderId());
        var outboxMessage = outboxMessageResponse.get();
        var orderCancelledDomainEvent = completeCancelForOrder(restaurantApprovalResponse);
        var sagaStatus = orderSagaHelper.orderStatusToSagaStatus(orderCancelledDomainEvent.getOrder().getOrderStatus());

        approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(outboxMessage, orderCancelledDomainEvent.getOrder().getOrderStatus(), sagaStatus));
        paymentOutboxHelper.savePaymentOutboxMessage(
            orderDataMapper.orderCancelledEventToOrderPaymentEventPayload(orderCancelledDomainEvent),
                orderCancelledDomainEvent.getOrder().getOrderStatus(),
                sagaStatus,
                OutboxStatus.STARTED,
                UUID.fromString(restaurantApprovalResponse.getSagaId())
        );

        log.info("Order with id: {} is being cancelled", orderCancelledDomainEvent.getOrder().getId().getValue());
    }

    private OrderCancelledEvent completeCancelForOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
        var order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        var orderCancelledDomainEvent = orderDomainService.cancelOrderPayments(order, restaurantApprovalResponse.getFailureMessages());
        orderSagaHelper.save(order);
        return orderCancelledDomainEvent;
    }

    private Order completeApprovalForOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
        var order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        orderDomainService.approveOrder(order);
        orderSagaHelper.save(order);
        return order;
    }

    private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(
            OrderApprovalOutboxMessage outboxMessage,
            OrderStatus orderStatus,
            SagaStatus sagaStatus
    ) {
        outboxMessage.setProcessedAt(ZonedDateTime.now());
        outboxMessage.setOrderStatus(orderStatus);
        outboxMessage.setSagaStatus(sagaStatus);
        return outboxMessage;
    }

    private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(
            String sagaId,
            OrderStatus orderStatus,
            SagaStatus sagaStatus
    ) {
        var outboxMessageResponse = paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(sagaId),
                SagaStatus.PROCESSING
        );
        if (outboxMessageResponse.isEmpty()) {
            log.error("Payment outbox cannot be found in: {} state", SagaStatus.PROCESSING);
            throw new OrderDomainException("Payment outbox cannot be found in: " + SagaStatus.PROCESSING + " state");
        }
        var outboxMessage = outboxMessageResponse.get();
        outboxMessage.setProcessedAt(ZonedDateTime.now());
        outboxMessage.setOrderStatus(orderStatus);
        outboxMessage.setSagaStatus(sagaStatus);
        return outboxMessage;
    }

}
