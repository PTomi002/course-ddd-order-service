package hu.paulintamas.foodorderingsystem.order.service.messaging.listener.kafka;

import hu.paulintamas.foodorderingsystem.kafka.consumer.service.KafkaConsumer;
import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.*;
import hu.paulintamas.foodorderingsystem.order.service.messaging.mapper.OrderMessagingDataMapper;
import hu.paulintamas.foodorderingsystem.service.domain.ports.input.messagelistener.payment.PaymentResponseMessageListener;
import hu.paulintamas.foodorderingsystem.service.domain.ports.input.messagelistener.restaurantapproval.RestaurantApprovalMessageListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class RestaurantApprovalResponseKafkaListener implements KafkaConsumer<RestaurantApprovalResponseAvroModel> {
    private final RestaurantApprovalMessageListener restaurantApprovalMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}", topics = "${order-service.restaurant-approval-response-topic-name}")
    public void receive(
            @Payload List<RestaurantApprovalResponseAvroModel> messages,
            @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
            @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
            @Header(KafkaHeaders.OFFSET) List<Long> offsets
    ) {
        log.info(
                "{} number of restaurant approval responses received with keys: {}, partitions: {}, and offsets: {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString()
        );
        messages.forEach(message -> {
            if (OrderApprovalStatus.APPROVED == message.getOrderApprovalStatus()) {
                log.info("Processing approved order for order id: {}", message.getOrderId());
                restaurantApprovalMessageListener.orderApproved(orderMessagingDataMapper.restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(message));
            } else if (OrderApprovalStatus.REJECTED == message.getOrderApprovalStatus()) {
                log.info("Processing rejected order for order id: {}", message.getOrderId());
                restaurantApprovalMessageListener.orderRejected(orderMessagingDataMapper.restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(message));
            }
        });
    }
}
