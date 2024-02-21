package hu.paulintamas.foodorderingsystem.payment.service.domain.ports.input.message.listener;

import hu.paulintamas.foodorderingsystem.payment.service.domain.dto.PaymentRequest;

public interface PaymentRequestMessageListener {

    void completePayment(PaymentRequest paymentRequest);

    void cancelPayment(PaymentRequest paymentRequest);
}
