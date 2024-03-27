package hu.paulintamas.foodorderingsystem.restaurant.service.domain.mapper;

import hu.paulintamas.foodorderingsystem.domain.valueobject.Money;
import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderId;
import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderStatus;
import hu.paulintamas.foodorderingsystem.domain.valueobject.RestaurantId;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.dto.RestaurantApprovalRequest;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity.OrderDetail;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity.Product;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity.Restaurant;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.event.OrderApprovalEvent;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.outbox.model.OrderEventPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * [KNOWLEDGE]
 * Factory class in the DDD terminology.
 */
@Component
public class RestaurantDataMapper {

    public Restaurant restaurantApprovalRequestToRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {
        return Restaurant.builder()
                .id(RestaurantId.builder().value(UUID.fromString(restaurantApprovalRequest.getRestaurantId())).build())
                .orderDetail(
                        OrderDetail.builder()
                                .id(OrderId.builder().value(UUID.fromString(restaurantApprovalRequest.getOrderId())).build())
                                .products(restaurantApprovalRequest.getProducts().stream().map(product ->
                                                Product.builder()
                                                        .id(product.getId())
                                                        .quantity(product.getQuantity())
                                                        .build()
                                        ).collect(Collectors.toList())
                                )
                                .totalAmount(restaurantApprovalRequest.getPrice())
                                .orderStatus(OrderStatus.valueOf(restaurantApprovalRequest.getRestaurantOrderStatus().toString()))
                                .build()
                )
                .build();
    }

    public OrderEventPayload orderApprovalEventToOrderEventPayload(OrderApprovalEvent orderApprovalEvent) {
        return OrderEventPayload.builder()
                .orderId(orderApprovalEvent.getOrderApproval().getOrderId().getValue().toString())
                .restaurantId(orderApprovalEvent.getRestaurantId().getValue().toString())
                .orderApprovalStatus(orderApprovalEvent.getOrderApproval().getOrderApprovalStatus().name())
                .createdAt(orderApprovalEvent.getCreatedAt())
                .failureMessages(orderApprovalEvent.getFailureMessages())
                .build();
    }

}
