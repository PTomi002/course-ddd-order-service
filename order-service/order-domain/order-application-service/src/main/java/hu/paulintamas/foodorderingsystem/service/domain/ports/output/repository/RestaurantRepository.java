package hu.paulintamas.foodorderingsystem.service.domain.ports.output.repository;

import hu.paulintamas.foodorderingsystem.service.domain.entity.Restaurant;

import java.util.Optional;

public interface RestaurantRepository {
    Optional<Restaurant> findRestaurantInformation(Restaurant restaurant);
}
