package hu.paulintamas.foodorderingsystem.service.domain.ports.output.messagepublisher.restaurantapproval;

import hu.paulintamas.foodorderingsystem.domain.event.publisher.DomainEventPublisher;
import hu.paulintamas.foodorderingsystem.service.domain.event.OrderPaidEvent;

/**
 * [KNOWLEDGE-OUTBOX]
 * We dont need them as Outbox Table + Scheduler will handle the event publishing.
 */
//public interface OrderPaidRestaurantMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {
//}
