package hu.paulintamas.foodorderingsystem.order.service.messaging.publisher.kafka;

import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.PaymentRequestAvroModel;
import hu.paulintamas.foodorderingsystem.kafka.producer.KafkaMessageHelper;
import hu.paulintamas.foodorderingsystem.kafka.producer.service.KafkaProducer;
import hu.paulintamas.foodorderingsystem.order.service.messaging.mapper.OrderMessagingDataMapper;
import hu.paulintamas.foodorderingsystem.outbox.OutboxStatus;
import hu.paulintamas.foodorderingsystem.service.domain.config.OrderServiceConfigData;
import hu.paulintamas.foodorderingsystem.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import hu.paulintamas.foodorderingsystem.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.messagepublisher.payment.PaymentRequestMessagePublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
@AllArgsConstructor
public class OrderPaymentEventKafkaPublisher implements PaymentRequestMessagePublisher {
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    @Override
    public void publish(
            OrderPaymentOutboxMessage orderPaymentOutboxMessage,
            BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> outboxCallback
    ) {
        var orderPaymentEventPayload = kafkaMessageHelper.getPayload(orderPaymentOutboxMessage.getPayload(), OrderPaymentEventPayload.class);
        var sagaId = orderPaymentOutboxMessage.getSagaId().toString();

        log.info(
                "Received OrderPaymentOutboxMessage for order id: {} and saga id: {}",
                orderPaymentEventPayload.getOrderId(),
                sagaId
        );

        var paymentRequestAvroModel = orderMessagingDataMapper.orderPaymentEventToPaymentRequestAvroModel(sagaId, orderPaymentEventPayload);

        kafkaProducer.send(
                orderServiceConfigData.getPaymentRequestTopicName(),
                sagaId, // they will be ordered by saga id, so the whole flow will be in on e partition
                paymentRequestAvroModel,
                kafkaMessageHelper.getKafkaCallback(
                        orderServiceConfigData.getPaymentRequestTopicName(),
                        paymentRequestAvroModel,
                        orderPaymentEventPayload.getOrderId(),
                        "PaymentRequestAvroModel",
                        orderPaymentOutboxMessage,
                        outboxCallback
                )
        );

        log.info(
                "OrderPaymentOutboxMessage sent to kafka for order id: {} and saga id: {}",
                orderPaymentEventPayload.getOrderId(),
                sagaId
        );
    }

}
