package hu.paulintamas.foodorderingsystem.payment.service.domain;

import hu.paulintamas.foodorderingsystem.payment.service.domain.dto.PaymentRequest;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentEvent;
import hu.paulintamas.foodorderingsystem.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentRequestMessageListenerImpl implements PaymentRequestMessageListener {

    private final PaymentRequestHelper paymentRequestHelper;

    @Override
    public void completePayment(PaymentRequest paymentRequest) {
       paymentRequestHelper.persistPayment(paymentRequest);
//        fireEvent(paymentEvent);
    }

    @Override
    public void cancelPayment(PaymentRequest paymentRequest) {
        paymentRequestHelper.persistCancelPayment(paymentRequest);
//        fireEvent(paymentEvent);
    }

    private void fireEvent(PaymentEvent paymentEvent) {
        log.info(
                "Publishing payment event with payment id: {} and order id: {}",
                paymentEvent.getPayment().getId().getValue(),
                paymentEvent.getPayment().getOrderId().getValue()
        );
//        paymentEvent.fire();

//      [KNOWLEDGE-REFACTOR]: This would grow with each added event type -> refactor it with object-oriented principles.
//        if (paymentEvent instanceof PaymentCompletedEvent) {
//            paymentCompletedMessagePublisher.publish((PaymentCompletedEvent) paymentEvent);
//        } else if (paymentEvent instanceof PaymentCancelledEvent) {
//            paymentCancelledMessagePublisher.publish((PaymentCancelledEvent) paymentEvent);
//        } else {
//            paymentFailedMessagePublisher.publish((PaymentFailedEvent) paymentEvent);
//        }
    }
}
