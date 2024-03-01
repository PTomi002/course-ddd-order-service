package hu.paulintamas.foodorderingsystem.service.domain.outbox.scheduler.approval;

import hu.paulintamas.foodorderingsystem.outbox.OutboxScheduler;
import hu.paulintamas.foodorderingsystem.outbox.OutboxStatus;
import hu.paulintamas.foodorderingsystem.saga.SagaStatus;
import hu.paulintamas.foodorderingsystem.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import hu.paulintamas.foodorderingsystem.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import hu.paulintamas.foodorderingsystem.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class RestaurantApprovalOutboxCleanerScheduler implements OutboxScheduler {
    private final ApprovalOutboxHelper approvalOutboxHelper;

    @Override
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        // Ask for all completed outbox messages
        var outboxMessageResponse = approvalOutboxHelper.getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
          OutboxStatus.COMPLETED,
          SagaStatus.SUCCEEDED, SagaStatus.FAILED, SagaStatus.COMPENSATED
        );
        if (outboxMessageResponse.isPresent() && !outboxMessageResponse.get().isEmpty()) {
            var outboxMessages = outboxMessageResponse.get();
            log.info(
                    "Received {} OrderApprovalOutboxMessage for clean-up. The payloads: {}",
                    outboxMessages.size(),
                    outboxMessages.stream().map(OrderApprovalOutboxMessage::getPayload).collect(Collectors.joining(System.lineSeparator()))
            );
            outboxMessages.forEach(orderPaymentOutboxMessage ->
                    approvalOutboxHelper.deleteOutboxMessageByOutboxStatusAndSagaStatus(
                            OutboxStatus.COMPLETED,
                            SagaStatus.SUCCEEDED, SagaStatus.FAILED, SagaStatus.COMPENSATED
                    )
            );
            log.info("{} OrderApprovalOutboxMessage deleted!", outboxMessages.size());
            // Can create an archive method / dashboard here to save deleted messages and analyze them later if needed.
        }
    }
}
