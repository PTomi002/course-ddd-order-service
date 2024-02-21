package hu.paulintamas.foodorderingsystem.payment.service.domain.mapper;

import hu.paulintamas.foodorderingsystem.domain.valueobject.CustomerId;
import hu.paulintamas.foodorderingsystem.domain.valueobject.Money;
import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderId;
import hu.paulintamas.foodorderingsystem.payment.service.domain.dto.PaymentRequest;
import hu.paulintamas.foodorderingsystem.payment.service.domain.entity.Payment;
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
}
