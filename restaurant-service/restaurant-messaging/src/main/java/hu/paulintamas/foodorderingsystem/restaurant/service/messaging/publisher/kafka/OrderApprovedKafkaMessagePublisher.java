package hu.paulintamas.foodorderingsystem.restaurant.service.messaging.publisher.kafka;

import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import hu.paulintamas.foodorderingsystem.kafka.producer.KafkaMessageHelper;
import hu.paulintamas.foodorderingsystem.kafka.producer.service.KafkaProducer;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.config.RestaurantServiceConfigData;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.event.OrderApprovedEvent;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.ports.output.message.publisher.OrderApprovedMessagePublisher;
import hu.paulintamas.foodorderingsystem.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderApprovedKafkaMessagePublisher implements OrderApprovedMessagePublisher {

    private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;
    private final KafkaProducer<String, RestaurantApprovalResponseAvroModel> kafkaProducer;
    private final RestaurantServiceConfigData restaurantServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    public OrderApprovedKafkaMessagePublisher(RestaurantMessagingDataMapper restaurantMessagingDataMapper,
                                              KafkaProducer<String, RestaurantApprovalResponseAvroModel> kafkaProducer,
                                              RestaurantServiceConfigData restaurantServiceConfigData,
                                              KafkaMessageHelper kafkaMessageHelper) {
        this.restaurantMessagingDataMapper = restaurantMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.restaurantServiceConfigData = restaurantServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(OrderApprovedEvent orderApprovedEvent) {
        String orderId = orderApprovedEvent.getOrderApproval().getOrderId().getValue().toString();

        log.info("Received OrderApprovedEvent for order id: {}", orderId);

        try {
            RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModel =
                    restaurantMessagingDataMapper
                            .orderApprovedEventToRestaurantApprovalResponseAvroModel(orderApprovedEvent);

            kafkaProducer.send(restaurantServiceConfigData.getRestaurantApprovalResponseTopicName(),
                    orderId,
                    restaurantApprovalResponseAvroModel,
                    kafkaMessageHelper.getKafkaCallback(restaurantServiceConfigData
                                    .getRestaurantApprovalResponseTopicName(),
                            restaurantApprovalResponseAvroModel,
                            orderId,
                            "RestaurantApprovalResponseAvroModel"));

            log.info("RestaurantApprovalResponseAvroModel sent to kafka at: {}", System.nanoTime());
        } catch (Exception e) {
            log.error("Error while sending RestaurantApprovalResponseAvroModel message" +
                    " to kafka with order id: {}, error: {}", orderId, e.getMessage());
        }
    }

}
