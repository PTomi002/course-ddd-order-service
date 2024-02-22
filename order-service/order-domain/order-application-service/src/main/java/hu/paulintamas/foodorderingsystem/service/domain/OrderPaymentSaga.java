package hu.paulintamas.foodorderingsystem.service.domain;

import hu.paulintamas.foodorderingsystem.domain.event.EmptyEvent;
import hu.paulintamas.foodorderingsystem.saga.SagaStep;
import hu.paulintamas.foodorderingsystem.service.domain.dto.message.PaymentResponse;
import hu.paulintamas.foodorderingsystem.service.domain.event.OrderPaidEvent;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.messagepublisher.restaurantapproval.OrderPaidRestaurantMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * [KNOWLEDGE-SAGA] Saga Coordinator/Orchestrator class.
 * Flow:
 *  - Payment is PAID or the saga flow stops with an empty event.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaymentSaga implements SagaStep<PaymentResponse, OrderPaidEvent, EmptyEvent> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final OrderPaidRestaurantMessagePublisher orderPaidRestaurantMessagePublisher;

    @Override
    @Transactional
    public OrderPaidEvent process(PaymentResponse paymentResponse) {
        log.info(
                "Completing payment for order id: {}",
                paymentResponse.getOrderId()
        );
        var order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        var orderPaid = orderDomainService.payOrder(order, orderPaidRestaurantMessagePublisher);
        orderSagaHelper.save(order);
        log.info(
                "Order with id: {} is paid",
                order.getId().getValue()
        );
        return orderPaid;
    }

    @Override
    @Transactional
    public EmptyEvent rollback(PaymentResponse paymentResponse) {
        log.info(
                "Cancelling order with id: {}",
                paymentResponse.getOrderId()
        );
        var order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        orderDomainService.cancelOrder(order, paymentResponse.getFailureMessages());
        orderSagaHelper.save(order);
        log.info(
                "Order with id: {} is cancelled",
                order.getId().getValue()
        );
        return EmptyEvent.INSTANCE;
    }

}
