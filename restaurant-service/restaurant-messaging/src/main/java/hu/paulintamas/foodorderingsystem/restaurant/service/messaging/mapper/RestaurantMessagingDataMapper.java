package hu.paulintamas.foodorderingsystem.restaurant.service.messaging.mapper;

import hu.paulintamas.foodorderingsystem.domain.valueobject.Money;
import hu.paulintamas.foodorderingsystem.domain.valueobject.ProductId;
import hu.paulintamas.foodorderingsystem.domain.valueobject.RestaurantOrderStatus;
import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.OrderApprovalStatus;
import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.dto.RestaurantApprovalRequest;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity.Product;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.outbox.model.OrderEventPayload;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantMessagingDataMapper {

    public RestaurantApprovalRequest restaurantApprovalRequestAvroModelToRestaurantApproval(RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel) {
        return RestaurantApprovalRequest.builder()
                .id(restaurantApprovalRequestAvroModel.getId().toString())
                .sagaId(restaurantApprovalRequestAvroModel.getSagaId().toString())
                .restaurantId(restaurantApprovalRequestAvroModel.getRestaurantId().toString())
                .orderId(restaurantApprovalRequestAvroModel.getOrderId().toString())
                .restaurantOrderStatus(RestaurantOrderStatus.valueOf(restaurantApprovalRequestAvroModel.getRestaurantOrderStatus().name()))
                .products(restaurantApprovalRequestAvroModel.getProducts()
                        .stream().map(avroModel ->
                                Product.builder()
                                        .id(ProductId.builder().value(UUID.fromString(avroModel.getId())).build())
                                        .quantity(avroModel.getQuantity())
                                        .build())
                        .collect(Collectors.toList()))
                .price(Money.of(restaurantApprovalRequestAvroModel.getPrice()))
                .createdAt(ZonedDateTime.ofInstant(restaurantApprovalRequestAvroModel.getCreatedAt(), ZoneId.systemDefault()))
                .build();
    }

    public RestaurantApprovalResponseAvroModel orderEventPayloadToRestaurantApprovalResponseAvroModel(String sagaId, OrderEventPayload orderEventPayload) {
        return RestaurantApprovalResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.fromString(sagaId))
                .setOrderId(UUID.fromString(orderEventPayload.getOrderId()))
                .setRestaurantId(UUID.fromString(orderEventPayload.getRestaurantId()))
                .setCreatedAt(orderEventPayload.getCreatedAt().toInstant())
                .setOrderApprovalStatus(OrderApprovalStatus.valueOf(orderEventPayload.getOrderApprovalStatus()))
                .setFailureMessages(orderEventPayload.getFailureMessages())
                .build();
    }

}
