package hu.paulintamas.foodorderingsystem.restaurant.service.domain.ports.output.repository;

import hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity.Restaurant;

import java.util.Optional;

public interface RestaurantRepository {
    Optional<Restaurant> findRestaurantInformation(Restaurant restaurant);
}
