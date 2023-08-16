package hu.paulintamas.foodorderingsystem.service.domain.dto.track;

import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderStatus;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class TrackOrderResponse {
    @NotNull
    UUID orderTrackingId;
    @NotNull
    OrderStatus orderStatus;
    @Nullable
    List<String> failureMessages;
}
