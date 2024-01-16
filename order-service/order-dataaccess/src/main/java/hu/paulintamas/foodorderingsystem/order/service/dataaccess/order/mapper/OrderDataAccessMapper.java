package hu.paulintamas.foodorderingsystem.order.service.dataaccess.order.mapper;

import hu.paulintamas.foodorderingsystem.domain.valueobject.*;
import hu.paulintamas.foodorderingsystem.order.service.dataaccess.order.entity.OrderAddressEntity;
import hu.paulintamas.foodorderingsystem.order.service.dataaccess.order.entity.OrderEntity;
import hu.paulintamas.foodorderingsystem.order.service.dataaccess.order.entity.OrderItemEntity;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Order;
import hu.paulintamas.foodorderingsystem.service.domain.entity.OrderItem;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Product;
import hu.paulintamas.foodorderingsystem.service.domain.valueobject.OrderItemId;
import hu.paulintamas.foodorderingsystem.service.domain.valueobject.StreetAddress;
import hu.paulintamas.foodorderingsystem.service.domain.valueobject.TrackingId;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDataAccessMapper {

    public OrderEntity orderToOrderEntity(Order order) {
        var orderEntity = OrderEntity.builder()
                .id(order.getId().getValue())
                .customerId(order.getCustomerId().getValue())
                .restaurantId(order.getRestaurantId().getValue())
                .trackingId(order.getTrackingId().getValue())
                .address(deliveryAddressToAddressEntity(order.getStreetAddress()))
                .price(order.getPrice().getAmount())
                .items(orderItemstToOrderItemEntities(order.getOrderItems()))
                .orderStatus(order.getOrderStatus())
                .failureMessages(String.join(",", order.getErrors()))
                .build();
        orderEntity.getAddress().setOrder(orderEntity);
        orderEntity.getItems().forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));
        return orderEntity;
    }

    public Order orderEntityToOrder(OrderEntity orderEntity) {
        return Order.builder()
                .id(OrderId.builder().value(orderEntity.getId()).build())
                .customerId(CustomerId.builder().value(orderEntity.getCustomerId()).build())
                .restaurantId(RestaurantId.builder().value(orderEntity.getRestaurantId()).build())
                .streetAddress(addressEntityToDeliveryAddress(orderEntity.getAddress()))
                .price(Money.of(orderEntity.getPrice()))
                .orderItems(orderItemEntitiesToOrderItems(orderEntity.getItems()))
                .trackingId(TrackingId.builder().value(orderEntity.getTrackingId()).build())
                .orderStatus(orderEntity.getOrderStatus())
                .errors(orderEntity.getFailureMessages().isEmpty() ? List.of() : Arrays.asList(orderEntity.getFailureMessages().split(",")))
                .build();
    }

    private List<OrderItem> orderItemEntitiesToOrderItems(List<OrderItemEntity> items) {
        return items.stream()
                .map(orderItemEntity -> OrderItem.builder()
                        .id(OrderItemId.builder().value(orderItemEntity.getId()).build())
                        .product(Product.builder().id(ProductId.builder().value(orderItemEntity.getProductId()).build()).build())
                        .price(Money.of(orderItemEntity.getPrice()))
                        .quantity(orderItemEntity.getQuantity())
                        .subTotal(Money.of(orderItemEntity.getSubTotal()))
                        .build()
                )
                .collect(Collectors.toList());
    }

    private StreetAddress addressEntityToDeliveryAddress(OrderAddressEntity address) {
        return StreetAddress.builder()
                .value(address.getId())
                .postalCode(address.getPostalCode())
                .street(address.getStreet())
                .city(address.getCity())
                .build();
    }

    private List<OrderItemEntity> orderItemstToOrderItemEntities(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> OrderItemEntity.builder()
                        .id(orderItem.getId().getValue())
                        .productId(orderItem.getProduct().getId().getValue())
                        .price(orderItem.getPrice().getAmount())
                        .quantity(orderItem.getQuantity())
                        .subTotal(orderItem.getSubTotal().getAmount())
                        .build()
                )
                .collect(Collectors.toList());
    }

    private OrderAddressEntity deliveryAddressToAddressEntity(StreetAddress streetAddress) {
        return OrderAddressEntity.builder()
                .id(streetAddress.getValue())
                .street(streetAddress.getStreet())
                .postalCode(streetAddress.getPostalCode())
                .city(streetAddress.getCity())
                .build();
    }
}
