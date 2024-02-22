package hu.paulintamas.foodorderingsystem.restaurant.service.domain.ports.output.message.publisher;

import hu.paulintamas.foodorderingsystem.domain.event.publisher.DomainEventPublisher;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.event.OrderApprovedEvent;

public interface OrderApprovedMessagePublisher extends DomainEventPublisher<OrderApprovedEvent> {
}
