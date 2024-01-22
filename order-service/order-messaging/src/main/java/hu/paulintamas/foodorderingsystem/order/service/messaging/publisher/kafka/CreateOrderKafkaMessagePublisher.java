package hu.paulintamas.foodorderingsystem.order.service.messaging.publisher.kafka;

import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.PaymentRequestAvroModel;
import hu.paulintamas.foodorderingsystem.kafka.producer.service.KafkaProducer;
import hu.paulintamas.foodorderingsystem.order.service.messaging.mapper.OrderMessagingDataMapper;
import hu.paulintamas.foodorderingsystem.service.domain.config.OrderServiceConfigData;
import hu.paulintamas.foodorderingsystem.service.domain.event.OrderCreatedEvent;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.messagepublisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class CreateOrderKafkaMessagePublisher implements OrderCreatedPaymentRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final OrderKafkaMessageHelper orderKafkaMessageHelper;

    @Override
    public void publish(OrderCreatedEvent domainEvent) {
        var orderId = domainEvent.getOrder().getId().getValue().toString();
        log.info("Received OrderCreatedEvent for order id: " + orderId);

        try {
            var paymentRequestAvroModel = orderMessagingDataMapper.orderCreatedEventToPaymentRequestAvroModel(domainEvent);
            kafkaProducer.send(
                    orderServiceConfigData.getPaymentRequestTopicName(),
                    orderId,
                    paymentRequestAvroModel,
                    orderKafkaMessageHelper.getKafkaCallback(orderServiceConfigData.getPaymentResponseTopicName(), paymentRequestAvroModel, orderId, "PaymentRequestAvroModel")
            );
            log.info("PaymentRequestAvroModel sent to Kafka for order id: {}", paymentRequestAvroModel.getOrderId());
        } catch (Exception e) {
            log.error("Error while sending PaymentRequestAvroModel message to kafka with order id: {}", orderId, e);
        }
    }
}
