package hu.paulintamas.foodorderingsystem.service.domain.outbox.model.approval;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OrderApprovalEventPayload {
    @JsonProperty
    private ZonedDateTime createdAt;
    @JsonProperty
    private String orderId;
    @JsonProperty
    private String restaurantId;
    @JsonProperty
    private String restaurantOrderStatus;
    @JsonProperty
    private BigDecimal price;
    @JsonProperty
    private List<OrderApprovalEventProduct> products;
}
