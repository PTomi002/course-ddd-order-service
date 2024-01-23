package hu.paulintamas.foodorderingsystem.service.domain.dto.create;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderCommand {
    @NotNull
    UUID customerId;
    @NotNull
    UUID restaurantId;
    @NotNull
    BigDecimal price;
    @NotNull
    List<OrderItem> items;
    @NotNull
    OrderAddress address;
}
