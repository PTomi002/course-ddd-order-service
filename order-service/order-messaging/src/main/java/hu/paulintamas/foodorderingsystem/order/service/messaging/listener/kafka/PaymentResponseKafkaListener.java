package hu.paulintamas.foodorderingsystem.order.service.messaging.listener.kafka;

import hu.paulintamas.foodorderingsystem.kafka.consumer.service.KafkaConsumer;
import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.PaymentResponseAvroModel;
import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.PaymentStatus;
import hu.paulintamas.foodorderingsystem.order.service.messaging.mapper.OrderMessagingDataMapper;
import hu.paulintamas.foodorderingsystem.service.domain.ports.input.messagelistener.payment.PaymentResponseMessageListener;
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
public class PaymentResponseKafkaListener implements KafkaConsumer<PaymentResponseAvroModel> {
    private final PaymentResponseMessageListener paymentResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}", topics = "${order-service.payment-response-topic-name}")
    public void receive(
            @Payload List<PaymentResponseAvroModel> messages,
            @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
            @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
            @Header(KafkaHeaders.OFFSET) List<Long> offsets
    ) {
        log.info(
                "{} number of payment responses received with keys: {}, partitions: {}, and offsets: {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString()
        );
        messages.forEach(message -> {
            if (PaymentStatus.COMPLETED == message.getPaymentStatus()) {
                log.info("Processing successful payment for order id: {}", message.getOrderId());
                paymentResponseMessageListener.paymentCompleted(orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(message));
            } else if(PaymentStatus.CANCELLED == message.getPaymentStatus()) {
                log.info("Processing unsuccessful payment for order id: {}", message.getOrderId());
                paymentResponseMessageListener.paymentCancelled(orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(message));
            }
        });
    }
}
