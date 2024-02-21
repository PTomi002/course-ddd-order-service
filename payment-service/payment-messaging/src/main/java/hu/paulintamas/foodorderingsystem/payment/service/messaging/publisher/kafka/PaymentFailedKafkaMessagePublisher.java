package hu.paulintamas.foodorderingsystem.payment.service.messaging.publisher.kafka;

import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.PaymentResponseAvroModel;
import hu.paulintamas.foodorderingsystem.kafka.producer.KafkaMessageHelper;
import hu.paulintamas.foodorderingsystem.kafka.producer.service.KafkaProducer;
import hu.paulintamas.foodorderingsystem.payment.service.domain.config.PaymentServiceConfigData;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentCancelledEvent;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentFailedEvent;
import hu.paulintamas.foodorderingsystem.payment.service.domain.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import hu.paulintamas.foodorderingsystem.payment.service.domain.ports.output.message.publisher.PaymentFailedMessagePublisher;
import hu.paulintamas.foodorderingsystem.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * [KNOWLEDGE]
 * Do not generalise the different publishers, due to be able to handle them individually.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFailedKafkaMessagePublisher implements PaymentFailedMessagePublisher {

    private final PaymentMessagingDataMapper paymentMessagingDataMapper;
    private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;
    private final PaymentServiceConfigData paymentServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    @Override
    public void publish(PaymentFailedEvent domainEvent) {
        var orderId = domainEvent.getPayment().getOrderId().getValue().toString();
        try {
            log.info(
                    "Received PaymentFailedEvent for order id: {}",
                    orderId
            );
            var paymentResponse = paymentMessagingDataMapper.paymentFailedEventToPaymentResponseAvroModel(domainEvent);
            kafkaProducer.send(
                    paymentServiceConfigData.getPaymentResponseTopicName(),
                    orderId,
                    paymentResponse,
                    kafkaMessageHelper.getKafkaCallback(paymentServiceConfigData.getPaymentResponseTopicName(), paymentResponse, orderId, "PaymentRequestAvroModel")
            );
            log.info(
                    "PaymentResponseAvroModel sent to kafka for order id: {}",
                    orderId
            );
        } catch (Exception e) {
            log.error(
                    "Error while sending PaymentResponseAvroModel message to kafka with order id: {}",
                    orderId, e
            );
        }
    }
}
