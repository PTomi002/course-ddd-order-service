package hu.paulintamas.foodorderingsystem.service.domain.outbox.scheduler.payment;

import hu.paulintamas.foodorderingsystem.outbox.OutboxScheduler;
import hu.paulintamas.foodorderingsystem.outbox.OutboxStatus;
import hu.paulintamas.foodorderingsystem.saga.SagaStatus;
import hu.paulintamas.foodorderingsystem.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.messagepublisher.payment.PaymentRequestMessagePublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentOutboxScheduler implements OutboxScheduler {
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final PaymentRequestMessagePublisher paymentRequestMessagePublisher;

    @Override
    @Transactional
    @Scheduled(
            fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${order-service.outbox-scheduler-initial-delay}"
    )
    public void processOutboxMessage() {
        // Asking for order PENDING and order CANCELLING events.
        var outboxMessageResponse = paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.STARTED,
                SagaStatus.COMPENSATING, SagaStatus.STARTED
        );
        if (outboxMessageResponse.isPresent() && !outboxMessageResponse.get().isEmpty()) {
            var outboxMessages = outboxMessageResponse.get();
            log.info(
                    "Received {} OrderPaymentOutboxMessage with ids: {}, sending to message bus!",
                    outboxMessages.size(),
                    outboxMessages.stream().map(orderPaymentOutboxMessage -> orderPaymentOutboxMessage.getId().toString()).collect(Collectors.joining(","))
            );
            outboxMessages.forEach(orderPaymentOutboxMessage ->
                    paymentRequestMessagePublisher.publish(orderPaymentOutboxMessage, this::updateOutboxStatus)
            );
            log.info("{} OrderPaymentOutboxMessage sent to message bus!", outboxMessages.size());
        }
    }

    /**
     * [KNOWLEDGE-OUTBOX]
     * For the next schedule we wont get the same Outbox Messages, because we change its status later.
     * BUT at some rare cases it can happen we read more times the same outbox message and publish duplications to kafka:
     *  - when publish is slow and the scheduler runs twice before publishing to kafka
     *  - multiple order instance picks up at the same time the same message
     * To prevent this we may have locking for it, but that is not acceptable in a distributed, well scaling application.
     * So we will implement idempotent messages and consumers at the payment side. [KNOWLEDGE] Idempotent Consumer
     */
    private void updateOutboxStatus(OrderPaymentOutboxMessage orderPaymentOutboxMessage, OutboxStatus outboxStatus) {
        orderPaymentOutboxMessage.setOutboxStatus(outboxStatus);
        paymentOutboxHelper.save(orderPaymentOutboxMessage);
        log.info(
                "OrderPaymentOutboxMessage is updated with outbox status: {}",
                outboxStatus.name()
        );
    }

}
