package hu.paulintamas.foodorderingsystem.order.service.dataaccess.restaurant.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(RestaurantEntityId.class)
@Table(name = "order_restaurant_materialized_view", schema = "restaurant")
public class RestaurantEntity {

    @Id
    private UUID restaurantId;
    @Id
    private UUID productId;
    private String restaurantName;
    private Boolean restaurantActive;
    private String productName;
    private BigDecimal productPrice;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RestaurantEntity that = (RestaurantEntity) o;

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
