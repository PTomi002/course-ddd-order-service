package hu.paulintamas.foodorderingsystem.service.domain.mapper;

import hu.paulintamas.foodorderingsystem.domain.valueobject.*;
import hu.paulintamas.foodorderingsystem.service.domain.dto.create.CreateOrderCommand;
import hu.paulintamas.foodorderingsystem.service.domain.dto.create.CreateOrderResponse;
import hu.paulintamas.foodorderingsystem.service.domain.dto.create.OrderAddress;
import hu.paulintamas.foodorderingsystem.service.domain.dto.track.TrackOrderQuery;
import hu.paulintamas.foodorderingsystem.service.domain.dto.track.TrackOrderResponse;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Order;
import hu.paulintamas.foodorderingsystem.service.domain.entity.OrderItem;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Product;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Restaurant;
import hu.paulintamas.foodorderingsystem.service.domain.valueobject.StreetAddress;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * [KNOWLEDGE]
 * <p>
 * In  DDD terms this can be a factory of the domain objects.
 * <p>
 * One huge con for the DDD is that we have 2 mapping layers which requires a lot of code duplication.
 */
@Component
public class OrderDataMapper {

    public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
        return Restaurant.builder()
                .id(RestaurantId.builder().value(createOrderCommand.getRestaurantId()).build())
                .products(
                        createOrderCommand.getOrderItems()
                                .stream()
                                .map(orderItem ->
                                        Product.builder()
                                                .id(ProductId.builder().value(orderItem.getProductId()).build())
                                                .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();
    }

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .customerId(CustomerId.builder().value(createOrderCommand.getCustomerId()).build())
                .restaurantId(RestaurantId.builder().value(createOrderCommand.getRestaurantId()).build())
                .streetAddress(orderAddressToStreetAddress(createOrderCommand.getAddress()))
                .price(Money.of(createOrderCommand.getPrice()))
                .orderItems(orderItemsToOrderItemEntities(createOrderCommand.getOrderItems()))
                .build();
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order order, String message) {
        return CreateOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .message(message)
                .build();
    }

    public TrackOrderResponse orderToTrackOrderResponse(Order order){
        return TrackOrderResponse.builder()
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getErrors())
                .orderTrackingId(order.getTrackingId().getValue())
                .build();
    }

    private List<OrderItem> orderItemsToOrderItemEntities(List<hu.paulintamas.foodorderingsystem.service.domain.dto.create.OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem ->
                        OrderItem.builder()
                                .product(Product.builder().id(ProductId.builder().value(orderItem.getProductId()).build()).build())
                                .price(Money.of(orderItem.getPrice()))
                                .quantity(orderItem.getQuantity())
                                .subTotal(Money.of(orderItem.getSubTotal()))
                                .build()
                )
                .collect(Collectors.toList());
    }

    private StreetAddress orderAddressToStreetAddress(OrderAddress address) {
        return StreetAddress.builder()
                .value(UUID.randomUUID())
                .street(address.getStreet())
                .postalCode(address.getPostalCode())
                .city(address.getCity())
                .build();
    }

}
