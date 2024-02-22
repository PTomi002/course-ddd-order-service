package hu.paulintamas.foodorderingsystem.restaurant.service.domain.event;

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
public class OrderApprovedEvent extends OrderApprovalEvent {
    private final DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher;

    @Override
    public void fire() {
        orderApprovedEventDomainEventPublisher.publish(this);
    }
}
