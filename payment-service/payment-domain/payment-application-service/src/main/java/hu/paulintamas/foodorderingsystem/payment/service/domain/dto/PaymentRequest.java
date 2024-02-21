package hu.paulintamas.foodorderingsystem.payment.service.domain.dto;

import hu.paulintamas.foodorderingsystem.domain.valueobject.PaymentOrderStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;

@Value
@Builder
public class PaymentRequest {
    String id;
    String sagaId;
    String orderId;
    String customerId;
    BigDecimal price;
    Instant createdAt;
    PaymentOrderStatus paymentOrderStatus;
}
