package hu.paulintamas.foodorderingsystem.payment.service.domain.mapper;

import hu.paulintamas.foodorderingsystem.domain.valueobject.CustomerId;
import hu.paulintamas.foodorderingsystem.domain.valueobject.Money;
import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderId;
import hu.paulintamas.foodorderingsystem.payment.service.domain.dto.PaymentRequest;
import hu.paulintamas.foodorderingsystem.payment.service.domain.entity.Payment;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentEvent;
import hu.paulintamas.foodorderingsystem.payment.service.domain.outbox.model.OrderEventPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentDataMapper {

    public Payment paymentRequestModelToPayment(PaymentRequest paymentRequest) {
        return Payment.builder()
                .orderId(OrderId.builder().value(UUID.fromString(paymentRequest.getOrderId())).build())
                .customerId(CustomerId.builder().value(UUID.fromString(paymentRequest.getCustomerId())).build())
                .price(Money.of(paymentRequest.getPrice()))
                .build();
    }

    public OrderEventPayload paymentEventToOrderEventPayload(PaymentEvent paymentEvent) {
        return OrderEventPayload.builder()
                .paymentId(paymentEvent.getPayment().getId().getValue().toString())
                .customerId(paymentEvent.getPayment().getCustomerId().getValue().toString())
                .orderId(paymentEvent.getPayment().getOrderId().getValue().toString())
                .price(paymentEvent.getPayment().getPrice().getAmount())
                .createdAt(paymentEvent.getCreatedAt())
                .paymentStatus(paymentEvent.getPayment().getPaymentStatus().name())
                .failureMessages(paymentEvent.getFailureMessages())
                .build();
    }

}
