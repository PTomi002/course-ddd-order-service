package hu.paulintamas.foodorderingsystem.service.domain.event;

import hu.paulintamas.foodorderingsystem.domain.event.publisher.DomainEventPublisher;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import static lombok.AccessLevel.PRIVATE;

@Getter
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class OrderCreatedEvent extends OrderEvent {

    private final DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher;
    @Override
    public void fire() {
        orderCreatedEventDomainEventPublisher.publish(this);
    }
}
