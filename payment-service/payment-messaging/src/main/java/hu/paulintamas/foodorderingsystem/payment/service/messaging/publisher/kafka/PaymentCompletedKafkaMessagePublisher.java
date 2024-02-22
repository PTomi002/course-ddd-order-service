package hu.paulintamas.foodorderingsystem.payment.service.messaging.publisher.kafka;

import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.PaymentResponseAvroModel;
import hu.paulintamas.foodorderingsystem.kafka.producer.KafkaMessageHelper;
import hu.paulintamas.foodorderingsystem.kafka.producer.service.KafkaProducer;
import hu.paulintamas.foodorderingsystem.payment.service.domain.config.PaymentServiceConfigData;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentCompletedEvent;
import hu.paulintamas.foodorderingsystem.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import hu.paulintamas.foodorderingsystem.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCompletedKafkaMessagePublisher implements PaymentCompletedMessagePublisher {

    private final PaymentMessagingDataMapper paymentMessagingDataMapper;
    private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;
    private final PaymentServiceConfigData paymentServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    @Override
    public void publish(PaymentCompletedEvent domainEvent) {
        var orderId = domainEvent.getPayment().getOrderId().getValue().toString();
        try {
            log.info(
                    "Received PaymentCompletedEvent for order id: {}",
                    orderId
            );
            var paymentResponse = paymentMessagingDataMapper.paymentCompletedEventToPaymentResponseAvroModel(domainEvent);
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