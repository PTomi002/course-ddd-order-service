package hu.paulintamas.foodorderingsystem.service.domain;

import hu.paulintamas.foodorderingsystem.service.domain.dto.message.PaymentResponse;
import hu.paulintamas.foodorderingsystem.service.domain.ports.input.messagelistener.payment.PaymentResponseMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener {

    private final OrderPaymentSaga orderPaymentSaga;

    @Override
    public void paymentCompleted(PaymentResponse paymentResponse) {
        var orderPaidEvent = orderPaymentSaga.process(paymentResponse);
        log.info("Publishing OrderPaidEvent for order id: {}", paymentResponse.getOrderId());
        orderPaidEvent.fire();
    }

    @Override
    public void paymentCancelled(PaymentResponse paymentResponse) {
        orderPaymentSaga.rollback(paymentResponse);
        log.info(
                "Order is rolled back with failure messages: {} and with order id: {}",
                paymentResponse.getFailureMessages(),
                paymentResponse.getOrderId()
        );
    }
}
