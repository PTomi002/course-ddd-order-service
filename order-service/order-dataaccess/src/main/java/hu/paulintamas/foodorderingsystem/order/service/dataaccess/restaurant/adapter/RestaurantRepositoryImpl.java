package hu.paulintamas.foodorderingsystem.order.service.dataaccess.restaurant.adapter;

import hu.paulintamas.foodorderingsystem.order.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import hu.paulintamas.foodorderingsystem.order.service.dataaccess.restaurant.repository.RestaurantJpaRepository;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Restaurant;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepository {
    private final RestaurantJpaRepository restaurantJpaRepository;
    private final RestaurantDataAccessMapper restaurantDataAccessMapper;
    @Override
    public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {
        var restaurantProducts = restaurantDataAccessMapper.restaurantTorRestaurantProducts(restaurant);
        var restaurantEntities = restaurantJpaRepository.findByRestaurantIdAndProductId(restaurant.getId().getValue(), restaurantProducts);
        return restaurantEntities.map(restaurantDataAccessMapper::restaurantEntityToRestaurant);
    }
}
