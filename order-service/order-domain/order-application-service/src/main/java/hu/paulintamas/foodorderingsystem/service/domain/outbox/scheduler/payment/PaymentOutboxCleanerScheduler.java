package hu.paulintamas.foodorderingsystem.service.domain.outbox.scheduler.payment;

import hu.paulintamas.foodorderingsystem.outbox.OutboxScheduler;
import hu.paulintamas.foodorderingsystem.outbox.OutboxStatus;
import hu.paulintamas.foodorderingsystem.saga.SagaStatus;
import hu.paulintamas.foodorderingsystem.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentOutboxCleanerScheduler implements OutboxScheduler {
    private final PaymentOutboxHelper paymentOutboxHelper;

    @Override
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        // Ask for all completed outbox messages
        var outboxMessageResponse = paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
          OutboxStatus.COMPLETED,
          SagaStatus.SUCCEEDED, SagaStatus.FAILED, SagaStatus.COMPENSATED
        );
        if (outboxMessageResponse.isPresent() && !outboxMessageResponse.get().isEmpty()) {
            var outboxMessages = outboxMessageResponse.get();
            log.info(
                    "Received {} OrderPaymentOutboxMessage for clean-up. The payloads: {}",
                    outboxMessages.size(),
                    outboxMessages.stream().map(OrderPaymentOutboxMessage::getPayload).collect(Collectors.joining(System.lineSeparator()))
            );
            outboxMessages.forEach(orderPaymentOutboxMessage ->
                    paymentOutboxHelper.deleteOutboxMessageByOutboxStatusAndSagaStatus(
                            OutboxStatus.COMPLETED,
                            SagaStatus.SUCCEEDED, SagaStatus.FAILED, SagaStatus.COMPENSATED
                    )
            );
            log.info("{} OrderPaymentOutboxMessage deleted!", outboxMessages.size());
            // Can create an archive method here to save deleted messages and analyze them later if needed.
        }
    }
}
