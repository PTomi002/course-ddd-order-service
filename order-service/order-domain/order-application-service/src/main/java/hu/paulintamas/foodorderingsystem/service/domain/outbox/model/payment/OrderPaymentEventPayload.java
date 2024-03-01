package hu.paulintamas.foodorderingsystem.service.domain.outbox.model.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
