package hu.paulintamas.foodorderingsystem.service.domain.dto.create;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder
public class OrderItem {
    @NotNull
    UUID productId;
    @NotNull
    Integer quantity;
    @NotNull
    BigDecimal price;
    @NotNull
    BigDecimal subTotal;
}
