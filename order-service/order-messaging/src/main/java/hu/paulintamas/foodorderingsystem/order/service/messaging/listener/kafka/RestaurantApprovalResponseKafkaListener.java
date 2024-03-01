package hu.paulintamas.foodorderingsystem.order.service.messaging.listener.kafka;

import hu.paulintamas.foodorderingsystem.kafka.consumer.service.KafkaConsumer;
import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.*;
import hu.paulintamas.foodorderingsystem.order.service.messaging.mapper.OrderMessagingDataMapper;
import hu.paulintamas.foodorderingsystem.service.domain.exception.OrderNotFoundException;
import hu.paulintamas.foodorderingsystem.service.domain.ports.input.messagelistener.payment.PaymentResponseMessageListener;
import hu.paulintamas.foodorderingsystem.service.domain.ports.input.messagelistener.restaurantapproval.RestaurantApprovalMessageListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
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
            // [KNOWLEDGE-RETRY] Retry strategies are here.
            try {
                if (OrderApprovalStatus.APPROVED == message.getOrderApprovalStatus()) {
                    log.info("Processing approved order for order id: {}", message.getOrderId());
                    restaurantApprovalMessageListener.orderApproved(orderMessagingDataMapper.restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(message));
                } else if (OrderApprovalStatus.REJECTED == message.getOrderApprovalStatus()) {
                    log.info("Processing rejected order for order id: {}", message.getOrderId());
                    restaurantApprovalMessageListener.orderRejected(orderMessagingDataMapper.restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(message));
                }
            } catch (OptimisticLockingFailureException e) {
                // NO-OP: we wont retry it as it is a result of a duplicate message passed idempotency check, log and ignore it
                log.error("Caught optimistic licking exception!", e);
            } catch (OrderNotFoundException e) {
                // NO-OP: we wont find the Order for the subsequent tries
                log.error("Order not found!", e);
            }
            // propagate all other exception and retry with the same fetched batch without ack them
        });
    }
}
