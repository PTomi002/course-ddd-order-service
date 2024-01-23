package hu.paulintamas.foodorderingsystem.service.domain.dto.create;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
