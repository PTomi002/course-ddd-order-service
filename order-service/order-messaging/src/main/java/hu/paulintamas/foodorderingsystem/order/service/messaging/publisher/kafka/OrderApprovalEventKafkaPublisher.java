package hu.paulintamas.foodorderingsystem.order.service.messaging.publisher.kafka;

import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import hu.paulintamas.foodorderingsystem.kafka.producer.KafkaMessageHelper;
import hu.paulintamas.foodorderingsystem.kafka.producer.service.KafkaProducer;
import hu.paulintamas.foodorderingsystem.order.service.messaging.mapper.OrderMessagingDataMapper;
import hu.paulintamas.foodorderingsystem.outbox.OutboxStatus;
import hu.paulintamas.foodorderingsystem.service.domain.config.OrderServiceConfigData;
import hu.paulintamas.foodorderingsystem.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import hu.paulintamas.foodorderingsystem.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.messagepublisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
@AllArgsConstructor
public class OrderApprovalEventKafkaPublisher implements RestaurantApprovalRequestMessagePublisher {
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    @Override
    public void publish(
            OrderApprovalOutboxMessage orderApprovalOutboxMessage,
            BiConsumer<OrderApprovalOutboxMessage, OutboxStatus> outboxCallback
    ) {
        var orderApprovalEventPayload = kafkaMessageHelper.getPayload(orderApprovalOutboxMessage.getPayload(), OrderApprovalEventPayload.class);
        var sagaId = orderApprovalOutboxMessage.getSagaId().toString();

        log.info(
                "Received OrderApprovalEventPayload for order id: {} and saga id: {}",
                orderApprovalEventPayload.getOrderId(),
                sagaId
        );

        try {
            var restaurantApprovalRequestAvroModel = orderMessagingDataMapper.orderApprovalEventToRestaurantApprovalRequestAvroModel(sagaId, orderApprovalEventPayload);

            kafkaProducer.send(
                    orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                    sagaId, // they will be ordered by saga id, so the whole flow will be in one partition
                    restaurantApprovalRequestAvroModel,
                    kafkaMessageHelper.getKafkaCallback(
                            orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                            restaurantApprovalRequestAvroModel,
                            orderApprovalEventPayload.getOrderId(),
                            "RestaurantApprovalRequestAvroModel",
                            orderApprovalOutboxMessage,
                            outboxCallback
                    )
            );

            log.info(
                    "OrderApprovalOutboxMessage sent to kafka for order id: {} and saga id: {}",
                    orderApprovalEventPayload.getOrderId(),
                    sagaId
            );
        } catch (Exception e) {
            log.error(
                    "Error while sending OrderApprovalEventPayload to kafka for order id: {} and saga id: {} and error: {}",
                    orderApprovalEventPayload.getOrderId(), sagaId, e.getMessage()
            );
        }
    }

}
