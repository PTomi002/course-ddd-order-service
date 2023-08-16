package hu.paulintamas.foodorderingsystem.service.domain.dto.message;

import hu.paulintamas.foodorderingsystem.domain.valueobject.PaymentStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Value
@Builder
public class PaymentResponse {
    String id;
    String sagaId;
    String orderId;
    String paymentId;
    String customerId;
    BigDecimal price;
    Instant createdAt;
    PaymentStatus paymentStatus;
    List<String> failureMessages;
}
