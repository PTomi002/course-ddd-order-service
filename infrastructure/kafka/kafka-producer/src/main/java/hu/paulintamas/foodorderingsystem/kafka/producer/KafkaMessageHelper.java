package hu.paulintamas.foodorderingsystem.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.paulintamas.foodorderingsystem.outbox.OutboxStatus;
import hu.paulintamas.foodorderingsystem.service.domain.exception.OrderDomainException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

import static java.util.Objects.isNull;

@Slf4j
@Component
@AllArgsConstructor
public class KafkaMessageHelper {

    private final ObjectMapper objectMapper;

    public <T, U> BiConsumer<SendResult<String, T>, Throwable> getKafkaCallback(
            String topicName,
            T model,
            String orderId,
            String modelName,
            U outboxMessage,
            BiConsumer<U, OutboxStatus> outboxCallback
    ) {
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
                outboxCallback.accept(outboxMessage, OutboxStatus.COMPLETED);
            } else {
                log.error("Error while sending: {} message: {} to topic: {}", modelName, model.toString(), topicName, error);
                outboxCallback.accept(outboxMessage, OutboxStatus.FAILED);
            }
        };
    }

    public <T> T getPayload(String payload, Class<T> clazz) {
        try {
            return objectMapper.readValue(payload, clazz);
        } catch (JsonProcessingException e) {
            log.error("Could not read: {} object!", clazz.getSimpleName(), e);
            throw new OrderDomainException("Could not read " + clazz.getSimpleName() +" object!", e);
        }
    }

}
