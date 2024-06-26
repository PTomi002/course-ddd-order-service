package hu.paulintamas.foodorderingsystem.service.domain.outbox.model.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * [KNOWLEDGE-OUTBOX]
 * Raw payloads saved next to the outbox messages.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderPaymentEventPayload {
    @JsonProperty
    private ZonedDateTime createdAt;
    @JsonProperty
    private String orderId;
    @JsonProperty
    private String customerId;
    @JsonProperty
    private String paymentOrderStatus;
    @JsonProperty
    private BigDecimal price;
}
