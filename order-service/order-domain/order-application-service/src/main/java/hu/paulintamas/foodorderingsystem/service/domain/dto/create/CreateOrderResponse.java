package hu.paulintamas.foodorderingsystem.service.domain.dto.create;

import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class CreateOrderResponse {
    @NotNull
    UUID orderTrackingId;
    @NotNull
    OrderStatus orderStatus;
    @NotNull
    String message;
}
