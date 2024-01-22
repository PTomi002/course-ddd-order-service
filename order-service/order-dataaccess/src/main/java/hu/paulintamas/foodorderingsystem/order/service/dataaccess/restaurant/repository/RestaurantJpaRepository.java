package hu.paulintamas.foodorderingsystem.order.service.dataaccess.restaurant.repository;

import hu.paulintamas.foodorderingsystem.order.service.dataaccess.restaurant.entity.RestaurantEntity;
import hu.paulintamas.foodorderingsystem.order.service.dataaccess.restaurant.entity.RestaurantEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantJpaRepository extends JpaRepository<RestaurantEntity, RestaurantEntityId> {
    Optional<List<RestaurantEntity>> findByRestaurantIdAndProductIdIn(UUID restaurantId, List<UUID> productId);
}
