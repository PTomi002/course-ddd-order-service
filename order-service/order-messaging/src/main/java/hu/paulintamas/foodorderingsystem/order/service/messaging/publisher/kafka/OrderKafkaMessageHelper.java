package hu.paulintamas.foodorderingsystem.order.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class OrderKafkaMessageHelper {

    public <T> BiConsumer<SendResult<String, T>, Throwable> getKafkaCallback(String topicName, T model, String orderId, String modelName) {
        return (result, error) -> {
            if (isNull(error)) {
                var metadata = result.getRecordMetadata();
                log.info(
                        "Received successful response from Kafka for order id: {} topic: {} partition: {} offset: {} timestamp: {}",
                        orderId,
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp()
                );
            } else {
                log.error("Error while sending: {} message: {} to topic: {}", modelName, model.toString(), topicName, error);
            }
        };
    }

}
