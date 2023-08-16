package hu.paulintamas.foodorderingsystem.service.domain.dto.create;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class CreateOrderCommand {
    @NotNull
    UUID customerId;
    @NotNull
    UUID restaurantId;
    @NotNull
    BigDecimal price;
    @NotNull
    List<OrderItem> orderItems;
    @NotNull
    OrderAddress address;
}
