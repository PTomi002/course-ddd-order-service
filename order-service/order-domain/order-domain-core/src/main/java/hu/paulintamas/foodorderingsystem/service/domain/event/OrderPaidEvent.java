package hu.paulintamas.foodorderingsystem.service.domain.event;

import hu.paulintamas.foodorderingsystem.domain.event.publisher.DomainEventPublisher;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import static lombok.AccessLevel.PRIVATE;

@Getter
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class OrderPaidEvent extends OrderEvent {
    private final DomainEventPublisher<OrderPaidEvent> orderPaidEventDomainEventPublisher;
    @Override
    public void fire() {
        orderPaidEventDomainEventPublisher.publish(this);
    }
}
