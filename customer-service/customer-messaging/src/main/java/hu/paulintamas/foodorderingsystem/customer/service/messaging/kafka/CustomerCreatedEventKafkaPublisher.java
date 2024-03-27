package hu.paulintamas.foodorderingsystem.customer.service.messaging.kafka;

import hu.paulintamas.foodorderingsystem.customer.service.domain.config.CustomerServiceConfigData;
import hu.paulintamas.foodorderingsystem.customer.service.domain.event.CustomerCreatedEvent;
import hu.paulintamas.foodorderingsystem.customer.service.domain.ports.output.message.publisher.CustomerMessagePublisher;
import hu.paulintamas.foodorderingsystem.customer.service.messaging.mapper.CustomerMessagingDataMapper;
import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.CustomerAvroModel;
import hu.paulintamas.foodorderingsystem.kafka.producer.service.KafkaProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

import static java.util.Objects.isNull;

@Slf4j
@Component
@AllArgsConstructor
public class CustomerCreatedEventKafkaPublisher implements CustomerMessagePublisher {

    private final CustomerMessagingDataMapper customerMessagingDataMapper;

    private final KafkaProducer<String, CustomerAvroModel> kafkaProducer;

    private final CustomerServiceConfigData customerServiceConfigData;

    @Override
    public void publish(CustomerCreatedEvent customerCreatedEvent) {
        log.info(
                "Received CustomerCreatedEvent for customer id: {}",
                customerCreatedEvent.getCustomer().getId().getValue()
        );
        try {
            var customerAvroModel = customerMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(customerCreatedEvent);
            kafkaProducer.send(
                    customerServiceConfigData.getCustomerTopicName(),
                    customerAvroModel.getId().toString(),
                    customerAvroModel,
                    getKafkaCallback(customerServiceConfigData.getCustomerTopicName(), customerAvroModel)
            );
            log.info(
                    "CustomerCreatedEvent sent to kafka for customer id: {}",
                    customerAvroModel.getId()
            );
        } catch (Exception e) {
            log.error(
                    "Error while sending CustomerCreatedEvent to kafka for customer id: {}, error: {}",
                    customerCreatedEvent.getCustomer().getId().getValue(), e.getMessage()
            );
        }
    }

    public <T, U> BiConsumer<SendResult<String, T>, Throwable> getKafkaCallback(String topicName, CustomerAvroModel message) {
        return (result, error) -> {
            if (isNull(error)) {
                var metadata = result.getRecordMetadata();
                log.info(
                        "Received new metadata. Topic: {}; Partition {}; Offset {}; Timestamp {}, at time {}",
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp(),
                        System.nanoTime()
                );
            } else {
                log.error("Error while sending message {} to topic {}", message.toString(), topicName, error);
            }
        };
    }

}
