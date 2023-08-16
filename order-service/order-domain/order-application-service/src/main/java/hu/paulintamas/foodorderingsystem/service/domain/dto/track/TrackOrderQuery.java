package hu.paulintamas.foodorderingsystem.service.domain.dto.track;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class TrackOrderQuery {
    @NotNull
    UUID orderTrackingId;
}
