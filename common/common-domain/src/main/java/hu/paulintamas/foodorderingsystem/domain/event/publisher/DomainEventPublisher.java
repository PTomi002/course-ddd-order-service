package hu.paulintamas.foodorderingsystem.domain.event.publisher;

import hu.paulintamas.foodorderingsystem.domain.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent> {
    void publish(T domainEvent);
}
