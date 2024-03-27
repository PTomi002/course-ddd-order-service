package hu.paulintamas.foodorderingsystem.payment.service.domain.outbox.scheduler;

import hu.paulintamas.foodorderingsystem.outbox.OutboxScheduler;
import hu.paulintamas.foodorderingsystem.outbox.OutboxStatus;
import hu.paulintamas.foodorderingsystem.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class OrderOutboxScheduler implements OutboxScheduler {
    private final OrderOutboxHelper orderOutboxHelper;
    private final PaymentResponseMessagePublisher paymentResponseMessagePublisher;

    @Override
    @Transactional
    @Scheduled(fixedRateString = "${payment-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${payment-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        var outboxMessagesResponse = orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.STARTED);
        if (outboxMessagesResponse.isPresent() && !outboxMessagesResponse.get().isEmpty()) {
            var outboxMessages = outboxMessagesResponse.get();
            log.info(
                    "Received {} OrderOutboxMessage with ids {}, sending to message bus!",
                    outboxMessages.size(),
                    outboxMessages.stream().map(outboxMessage -> outboxMessage.getId().toString()).collect(Collectors.joining(","))
            );
            outboxMessages.forEach(orderOutboxMessage ->
                    paymentResponseMessagePublisher.publish(orderOutboxMessage, orderOutboxHelper::updateOutboxMessage)
            );
            log.info("{} OrderOutboxMessage sent to message bus!", outboxMessages.size());
        }
    }
}
