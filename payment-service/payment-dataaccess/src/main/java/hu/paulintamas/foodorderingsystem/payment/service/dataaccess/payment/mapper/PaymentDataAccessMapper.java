package hu.paulintamas.foodorderingsystem.payment.service.dataaccess.payment.mapper;

import hu.paulintamas.foodorderingsystem.domain.valueobject.CustomerId;
import hu.paulintamas.foodorderingsystem.domain.valueobject.Money;
import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderId;
import hu.paulintamas.foodorderingsystem.payment.service.dataaccess.payment.entity.PaymentEntity;
import hu.paulintamas.foodorderingsystem.payment.service.domain.entity.Payment;
import hu.paulintamas.foodorderingsystem.payment.service.domain.valueobject.PaymentId;
import org.springframework.stereotype.Component;

@Component
public class PaymentDataAccessMapper {

    public PaymentEntity paymentToPaymentEntity(Payment payment) {
        return PaymentEntity.builder()
                .id(payment.getId().getValue())
                .customerId(payment.getCustomerId().getValue())
                .orderId(payment.getOrderId().getValue())
                .price(payment.getPrice().getAmount())
                .status(payment.getPaymentStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public Payment paymentEntityToPayment(PaymentEntity paymentEntity) {
        return Payment.builder()
                .id(PaymentId.builder().value(paymentEntity.getId()).build())
                .customerId(CustomerId.builder().value(paymentEntity.getCustomerId()).build())
                .orderId(OrderId.builder().value(paymentEntity.getOrderId()).build())
                .price(Money.of(paymentEntity.getPrice()))
                .createdAt(paymentEntity.getCreatedAt())
                .build();
    }

}
