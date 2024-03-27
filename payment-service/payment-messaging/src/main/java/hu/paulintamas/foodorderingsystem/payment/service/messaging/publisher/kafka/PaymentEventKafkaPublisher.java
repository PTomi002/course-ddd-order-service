package hu.paulintamas.foodorderingsystem.payment.service.messaging.publisher.kafka;

import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.PaymentResponseAvroModel;
import hu.paulintamas.foodorderingsystem.kafka.producer.KafkaMessageHelper;
import hu.paulintamas.foodorderingsystem.kafka.producer.service.KafkaProducer;
import hu.paulintamas.foodorderingsystem.outbox.OutboxStatus;
import hu.paulintamas.foodorderingsystem.payment.service.domain.config.PaymentServiceConfigData;
import hu.paulintamas.foodorderingsystem.payment.service.domain.outbox.model.OrderEventPayload;
import hu.paulintamas.foodorderingsystem.payment.service.domain.outbox.model.OrderOutboxMessage;
import hu.paulintamas.foodorderingsystem.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
import hu.paulintamas.foodorderingsystem.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentEventKafkaPublisher implements PaymentResponseMessagePublisher {
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;
    private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;
    private final PaymentServiceConfigData paymentServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    @Override
    public void publish(OrderOutboxMessage orderOutboxMessage, BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback) {
        var orderEventPayload = kafkaMessageHelper.getPayload(orderOutboxMessage.getPayload(), OrderEventPayload.class);
        var sagaId = orderOutboxMessage.getSagaId().toString();
        log.info(
                "Received OrderOutboxMessage for order id: {} and saga id: {}",
                orderEventPayload.getOrderId(), sagaId
        );
        try {
            var paymentResponseAvroModel = paymentMessagingDataMapper.orderEventPayloadToPaymentResponseAvroModel(sagaId, orderEventPayload);
            kafkaProducer.send(
                    paymentServiceConfigData.getPaymentResponseTopicName(),
                    sagaId,
                    paymentResponseAvroModel,
                    kafkaMessageHelper.getKafkaCallback(
                            paymentServiceConfigData.getPaymentResponseTopicName(),
                            paymentResponseAvroModel,
                            orderEventPayload.getOrderId(),
                            "PaymentResponseAvroModel",
                            orderOutboxMessage,
                            outboxCallback
                    )
            );

            log.info(
                    "PaymentResponseAvroModel sent to kafka for order id: {} and saga id: {}",
                    paymentResponseAvroModel.getOrderId(), sagaId
            );
        } catch (Exception e) {
            log.error(
                    "Error while sending PaymentRequestAvroModel message to kafka with order id: {} and saga id: {}, error: {}",
                    orderEventPayload.getOrderId(), sagaId, e.getMessage()
            );
        }
    }
}
