package hu.paulintamas.foodorderingsystem.order.service.dataaccess.restaurant.mapper;

import hu.paulintamas.foodorderingsystem.domain.valueobject.Money;
import hu.paulintamas.foodorderingsystem.domain.valueobject.ProductId;
import hu.paulintamas.foodorderingsystem.domain.valueobject.RestaurantId;
import hu.paulintamas.foodorderingsystem.order.service.dataaccess.restaurant.entity.RestaurantEntity;
import hu.paulintamas.foodorderingsystem.order.service.dataaccess.restaurant.exception.RestaurantDataAccessException;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Product;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataAccessMapper {
    public List<UUID> restaurantTorRestaurantProducts(Restaurant restaurant) {
        return restaurant.getProducts().stream().map(product -> product.getId().getValue()).collect(Collectors.toList());
    }

    public Restaurant restaurantEntityToRestaurant(List<RestaurantEntity> restaurantEntities) {
        var restaurantEntity = restaurantEntities.stream().findFirst().orElseThrow(() -> new RestaurantDataAccessException("Restaurant could not be found"));
        var restaurantProducts = restaurantEntities.stream().map(entity ->
                        Product.builder()
                                .id(ProductId.builder().value(entity.getProductId()).build())
                                .name(entity.getProductName())
                                .price(Money.of(entity.getProductPrice()))
                                .build()
                )
                .collect(Collectors.toList());
        return Restaurant.builder()
                .id(RestaurantId.builder().value(restaurantEntity.getRestaurantId()).build())
                .products((List<Product>) restaurantProducts)
                .active(restaurantEntity.getRestaurantActive())
                .build();
    }

}
