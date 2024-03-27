package hu.paulintamas.foodorderingsystem.payment.service.messaging.listener.kafka;

import hu.paulintamas.foodorderingsystem.kafka.consumer.service.KafkaConsumer;
import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.PaymentOrderStatus;
import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.PaymentRequestAvroModel;
import hu.paulintamas.foodorderingsystem.payment.service.domain.exception.PaymentDomainException;
import hu.paulintamas.foodorderingsystem.payment.service.domain.exception.PaymentNotFoundException;
import hu.paulintamas.foodorderingsystem.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import hu.paulintamas.foodorderingsystem.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.jdbc.PSQLSavepoint;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestListener implements KafkaConsumer<PaymentRequestAvroModel> {

    private final PaymentRequestMessageListener paymentRequestMessageListener;
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;

    @Override
    @KafkaListener(
            id = "${kafka-consumer-config.payment-consumer-group-id}",
            topics = "${payment-service.payment-request-topic-name}"
    )
    public void receive(
            @Payload List<PaymentRequestAvroModel> messages,
            @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
            @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
            @Header(KafkaHeaders.OFFSET) List<Long> offsets
    ) {
        messages.forEach(message -> {
            try {
                if (PaymentOrderStatus.PENDING == message.getPaymentOrderStatus()) {
                    log.info(
                            "Processing payment for order id: {}",
                            message.getOrderId()
                    );
                    paymentRequestMessageListener.completePayment(paymentMessagingDataMapper.paymentRequestAvroModelToPaymentRequest(message));
                } else if (PaymentOrderStatus.CANCELLED == message.getPaymentOrderStatus()) {
                    log.info(
                            "Cancelling payment for order id: {}",
                            message.getOrderId()
                    );
                    paymentRequestMessageListener.cancelPayment(paymentMessagingDataMapper.paymentRequestAvroModelToPaymentRequest(message));
                }
            } catch(DataAccessException e ) {
                // [KNOWLEDGE] Idempotent Consumer
                // Here we don't have optimistic locking exception, instead asserting for unique index violation (duplicate insert),
                //      because we use OptimisticLockingException for checking an already existing record,
                //      and use UniqueIndexViolation for inserting from concurrent threads (record does not exist yet).
                var root = (SQLException) e.getRootCause();
                if (root != null && root.getSQLState() != null && PSQLState.UNIQUE_VIOLATION.getState().equals(root.getSQLState())) {
                    log.error(
                            "Caught unique index violation exception with sal state: {} for order id: {}",
                            root.getSQLState(), message.getOrderId()
                    );
                } else {
                    // propagate error and retry
                    throw new PaymentDomainException("Caught unique index violation exception with sal state: " + root.getSQLState() +  " for order id: " + message.getOrderId());
                }
            } catch (PaymentNotFoundException e) {
                // NO-OP: it is not a valid case for a saga flow
                log.error(
                        "No payment found for order id: {}",
                        message.getOrderId()
                );
            }
        });
    }
}
