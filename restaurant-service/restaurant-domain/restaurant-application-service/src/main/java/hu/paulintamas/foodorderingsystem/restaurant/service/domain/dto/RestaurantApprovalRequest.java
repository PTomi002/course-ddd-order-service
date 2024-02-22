package hu.paulintamas.foodorderingsystem.restaurant.service.domain.dto;

import hu.paulintamas.foodorderingsystem.domain.valueobject.Money;
import hu.paulintamas.foodorderingsystem.domain.valueobject.RestaurantOrderStatus;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity.Product;
import lombok.Builder;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.List;

@Value
@Builder
public class RestaurantApprovalRequest {
    String id;
    String sagaId;
    String restaurantId;
    String orderId;
    RestaurantOrderStatus restaurantOrderStatus;
    List<Product> products;
    Money price;
    ZonedDateTime createdAt;
}
