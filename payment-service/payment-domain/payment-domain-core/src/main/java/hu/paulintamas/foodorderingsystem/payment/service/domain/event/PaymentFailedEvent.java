package hu.paulintamas.foodorderingsystem.payment.service.domain.event;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import static lombok.AccessLevel.PRIVATE;

@Getter
@SuperBuilder
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class PaymentFailedEvent extends PaymentEvent {

    /**
     * [KNOWLEDGE-REFACTOR] Should not implement fire() event in the domain-core package, because app domain module shoot the DomainEvent(s), domain core only creates them.
     */
//    private final DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher;

//    @Override
//    public void fire() {
//        paymentFailedEventDomainEventPublisher.publish(this);
//    }
}
