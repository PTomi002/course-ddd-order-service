package hu.paulintamas.foodorderingsystem.restaurant.service.messaging.listener.kafka;

import hu.paulintamas.foodorderingsystem.kafka.consumer.service.KafkaConsumer;
import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.RestaurantApprovalRequestHelper;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.exception.RestaurantApplicationServiceException;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.exception.RestaurantNotFoundException;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.ports.input.messagel.listener.RestaurantApprovalRequestMessageListener;
import hu.paulintamas.foodorderingsystem.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantApprovalRequestKafkaListener implements KafkaConsumer<RestaurantApprovalRequestAvroModel> {

    private final RestaurantApprovalRequestMessageListener restaurantApprovalRequestMessageListener;
    private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;

    @Override
    @KafkaListener(
            id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
            topics = "${restaurant-service.restaurant-approval-request-topic-name}"
    )
    public void receive(@Payload List<RestaurantApprovalRequestAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of orders approval requests received with keys {}, partitions {} and offsets {}" +
                        ", sending for restaurant approval",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString());

        messages.forEach(restaurantApprovalRequestAvroModel -> {
            try {
                log.info("Processing order approval for order id: {}", restaurantApprovalRequestAvroModel.getOrderId());
                restaurantApprovalRequestMessageListener.approveOrder(restaurantMessagingDataMapper.
                        restaurantApprovalRequestAvroModelToRestaurantApproval(restaurantApprovalRequestAvroModel));
            } catch (DataAccessException e) {
                var root = (SQLException) e.getRootCause();
                if (root != null && root.getSQLState() != null && PSQLState.UNIQUE_VIOLATION.equals(root.getSQLState())) {
                    // NO-OP: for unique constraint violation, only for insertions
                    log.error(
                            "Caught unique constraint violation exception for order id: {}",
                            restaurantApprovalRequestAvroModel.getOrderId()
                    );
                } else {
                    throw new RestaurantApplicationServiceException("Throwing DataAccessException for order id: " + restaurantApprovalRequestAvroModel.getOrderId(), e);
                }
            } catch (RestaurantNotFoundException e) {
                // NO-OP: makes no sense to retry
                log.error(
                        "No restaurant found for restaurant id: {} and order id: {}",
                        restaurantApprovalRequestAvroModel.getRestaurantId(),
                        restaurantApprovalRequestAvroModel.getOrderId()
                );
            }
        });
    }

}
