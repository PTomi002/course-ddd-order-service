package hu.paulintamas.foodorderingsystem.service.domain.dto.create;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderAddress {
    @NotNull
    @Max(value = 50)
    String street;
    @NotNull
    @Max(value = 10)
    String postalCode;
    @NotNull
    @Max(value = 50)
    String city;
}
