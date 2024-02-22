package hu.paulintamas.foodorderingsystem.restaurant.service.dataaccess.mapper;

import hu.paulintamas.foodorderingsystem.dataaccess.restaurant.entity.RestaurantEntity;
import hu.paulintamas.foodorderingsystem.dataaccess.restaurant.exception.RestaurantDataAccessException;
import hu.paulintamas.foodorderingsystem.domain.valueobject.Money;
import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderId;
import hu.paulintamas.foodorderingsystem.domain.valueobject.ProductId;
import hu.paulintamas.foodorderingsystem.domain.valueobject.RestaurantId;
import hu.paulintamas.foodorderingsystem.restaurant.service.dataaccess.entity.OrderApprovalEntity;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity.OrderApproval;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity.OrderDetail;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity.Product;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity.Restaurant;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.valueobject.OrderApprovalId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataAccessMapper {

    public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
        return restaurant.getOrderDetail().getProducts().stream()
                .map(product -> product.getId().getValue())
                .collect(Collectors.toList());
    }

    public Restaurant restaurantEntityToRestaurant(List<RestaurantEntity> restaurantEntities) {
        RestaurantEntity restaurantEntity =
                restaurantEntities.stream().findFirst().orElseThrow(() ->
                        new RestaurantDataAccessException("No restaurants found!"));

        List<Product> restaurantProducts = restaurantEntities.stream().map(entity ->
                        Product.builder()
                                .id(ProductId.builder().value(entity.getProductId()).build())
                                .name(entity.getProductName())
                                .price(Money.of(entity.getProductPrice()))
                                .available(entity.isProductAvailable())
                                .build())
                .collect(Collectors.toList());

        return Restaurant.builder()
                .id(RestaurantId.builder().value(restaurantEntity.getRestaurantId()).build())
                .orderDetail(OrderDetail.builder()
                        .products(restaurantProducts)
                        .build())
                .active(restaurantEntity.getRestaurantActive())
                .build();
    }

    public OrderApprovalEntity orderApprovalToOrderApprovalEntity(OrderApproval orderApproval) {
        return OrderApprovalEntity.builder()
                .id(orderApproval.getId().getId())
                .restaurantId(orderApproval.getRestaurantId().getValue())
                .orderId(orderApproval.getOrderId().getValue())
                .status(orderApproval.getOrderApprovalStatus())
                .build();
    }

    public OrderApproval orderApprovalEntityToOrderApproval(OrderApprovalEntity orderApprovalEntity) {
        return OrderApproval.builder()
                .id(OrderApprovalId.builder().id(orderApprovalEntity.getId()).build())
                .restaurantId(RestaurantId.builder().value(orderApprovalEntity.getRestaurantId()).build())
                .orderId(OrderId.builder().value(orderApprovalEntity.getOrderId()).build())
                .orderApprovalStatus(orderApprovalEntity.getStatus())
                .build();
    }

}
