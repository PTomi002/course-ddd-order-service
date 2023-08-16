package hu.paulintamas.foodorderingsystem.service.domain.dto.message;

import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderApprovalStatus;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
public class RestaurantApprovalResponse {
    String id;
    String sagaId;
    String orderId;
    String restaurantId;
    Instant createdAt;
    OrderApprovalStatus orderApprovalStatus;
    List<String> failureMessages;
}
