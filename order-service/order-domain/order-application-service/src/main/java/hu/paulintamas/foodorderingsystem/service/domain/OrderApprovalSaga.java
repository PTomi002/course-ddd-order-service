package hu.paulintamas.foodorderingsystem.service.domain;

import hu.paulintamas.foodorderingsystem.domain.event.EmptyEvent;
import hu.paulintamas.foodorderingsystem.saga.SagaStep;
import hu.paulintamas.foodorderingsystem.service.domain.dto.message.RestaurantApprovalResponse;
import hu.paulintamas.foodorderingsystem.service.domain.event.OrderCancelledEvent;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.messagepublisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * [KNOWLEDGE-SAGA] Saga Coordinator/Orchestrator class.
 * Flow:
 *  - Order is also APPROVED from the restaurant side -> the flow is finished, or rollback with cancel event.
 * Impl. Details:
 *  - @Transactional -> Want the DB to finish successfully first to be consistent with the eventing out.
 *  - BUT even now the publishing can fail and leaving the system in inconsistent state.  -> Adding Outbox pattern next to Saga pattern.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse, EmptyEvent, OrderCancelledEvent> {

    private final OrderSagaHelper orderSagaHelper;
    private final OrderDomainService orderDomainService;
    private final OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher;

    @Override
    @Transactional
    public EmptyEvent process(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info(
                "Approving order with id: {}",
                restaurantApprovalResponse.getOrderId()
        );
        var order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        orderDomainService.approveOrder(order);
        orderSagaHelper.save(order);
        log.info(
                "Order with id: {} is approved",
                order.getId().getValue()
        );
        return EmptyEvent.INSTANCE;
    }

    @Override
    @Transactional
    public OrderCancelledEvent rollback(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info(
                "Cancelling order with id: {}",
                restaurantApprovalResponse.getOrderId()
        );
        var order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        var orderCancelled = orderDomainService.cancelOrderPayments(order, restaurantApprovalResponse.getFailureMessages(), orderCancelledPaymentRequestMessagePublisher);
        orderSagaHelper.save(order);
        log.info(
                "Order with id: {} is being cancelled",
                order.getId().getValue()
        );
        return orderCancelled;
    }
}
