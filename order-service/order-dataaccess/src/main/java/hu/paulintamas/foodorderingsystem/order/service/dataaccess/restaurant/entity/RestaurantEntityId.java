package hu.paulintamas.foodorderingsystem.order.service.dataaccess.restaurant.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantEntityId {

    private UUID restaurantId;
    private UUID productId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RestaurantEntityId that = (RestaurantEntityId) o;

        if (!Objects.equals(restaurantId, that.restaurantId)) return false;
        return Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        int result = restaurantId != null ? restaurantId.hashCode() : 0;
        result = 31 * result + (productId != null ? productId.hashCode() : 0);
        return result;
    }
}
