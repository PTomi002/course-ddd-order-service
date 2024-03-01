package hu.paulintamas.foodorderingsystem.service.domain.ports.output.messagepublisher.payment;

import hu.paulintamas.foodorderingsystem.domain.event.publisher.DomainEventPublisher;
import hu.paulintamas.foodorderingsystem.service.domain.event.OrderCreatedEvent;

/**
 * [KNOWLEDGE-OUTBOX]
 * We dont need them as Outbox Table + Scheduler will handle the event publishing.
 */
//public interface OrderCreatedPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCreatedEvent> {
//}
