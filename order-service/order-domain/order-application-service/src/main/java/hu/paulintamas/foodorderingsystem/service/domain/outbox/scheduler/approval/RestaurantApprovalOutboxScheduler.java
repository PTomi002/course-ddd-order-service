package hu.paulintamas.foodorderingsystem.service.domain.outbox.scheduler.approval;

import hu.paulintamas.foodorderingsystem.outbox.OutboxScheduler;
import hu.paulintamas.foodorderingsystem.outbox.OutboxStatus;
import hu.paulintamas.foodorderingsystem.saga.SagaStatus;
import hu.paulintamas.foodorderingsystem.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import hu.paulintamas.foodorderingsystem.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import hu.paulintamas.foodorderingsystem.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.messagepublisher.payment.PaymentRequestMessagePublisher;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.messagepublisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class RestaurantApprovalOutboxScheduler implements OutboxScheduler {
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final RestaurantApprovalRequestMessagePublisher restaurantApprovalRequestMessagePublisher;

    @Override
    @Transactional
    @Scheduled(
            fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${order-service.outbox-scheduler-initial-delay}"
    )
    public void processOutboxMessage() {
        // Asking for order PAID events.
        var outboxMessageResponse = approvalOutboxHelper.getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.STARTED,
                SagaStatus.PROCESSING
        );
        if (outboxMessageResponse.isPresent() && !outboxMessageResponse.get().isEmpty()) {
            var outboxMessages = outboxMessageResponse.get();
            log.info(
                    "Received {} OrderApprovalOutboxMessage with ids: {}, sending to message bus!",
                    outboxMessages.size(),
                    outboxMessages.stream().map(orderPaymentOutboxMessage -> orderPaymentOutboxMessage.getId().toString()).collect(Collectors.joining(","))
            );
            outboxMessages.forEach(orderApprovalOutboxMessage ->
                    restaurantApprovalRequestMessagePublisher.publish(orderApprovalOutboxMessage, this::updateOutboxStatus)
            );
            log.info("{} OrderPaymentOutboxMessage sent to message bus!", outboxMessages.size());
        }
    }

    private void updateOutboxStatus(OrderApprovalOutboxMessage orderApprovalOutboxMessage, OutboxStatus outboxStatus) {
        orderApprovalOutboxMessage.setOutboxStatus(outboxStatus);
        approvalOutboxHelper.save(orderApprovalOutboxMessage);
        log.info(
                "OrderApprovalOutboxMessage is updated with outbox status: {}",
                outboxStatus.name()
        );
    }

}
