package hu.paulintamas.foodorderingsystem.kafka.producer.service.impl;

import hu.paulintamas.foodorderingsystem.kafka.producer.exception.KafkaProducerException;
import hu.paulintamas.foodorderingsystem.kafka.producer.service.KafkaProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.util.function.BiConsumer;

@Slf4j
@Component
@AllArgsConstructor
public class KafkaProducerImpl<K extends Serializable, V extends SpecificRecordBase> implements KafkaProducer<K, V> {

    private final KafkaTemplate<K, V> kafkaTemplate;

    @Override
    public void send(String topic, K key, V message, BiConsumer<? super SendResult<K, V>, ? super Throwable> callback) {
        log.info("Message='Sending message to kafka', message= {}, topic = {}", message, topic);
        try {
            var result = kafkaTemplate.send(topic, key, message);
            result.whenComplete(callback);
        } catch (KafkaException kafkaException) {
            log.error("Message='Error on kafka producer', topic={}, key={}", topic, key, kafkaException);
            throw new KafkaProducerException("Error on kafka producer, topic=" + topic + ",key=" + key, kafkaException);
        }
    }

    @PreDestroy
    public void close() {
        if (kafkaTemplate != null) {
            log.info("Message=Closing kafka template resources");
            kafkaTemplate.destroy();
        }
    }

}
