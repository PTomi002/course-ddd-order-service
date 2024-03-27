package hu.paulintamas.foodorderingsystem.payment.service.domain.ports.output.message.publisher;

import hu.paulintamas.foodorderingsystem.outbox.OutboxStatus;
import hu.paulintamas.foodorderingsystem.payment.service.domain.outbox.model.OrderOutboxMessage;

import java.util.function.BiConsumer;

public interface PaymentResponseMessagePublisher {
    void publish(
            OrderOutboxMessage orderOutboxMessage,
            BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback
    );
}
