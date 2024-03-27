package hu.paulintamas.foodorderingsystem.payment.service.domain.event;

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
public class PaymentCompletedEvent extends PaymentEvent {

    /**
     * [KNOWLEDGE-REFACTOR] Should not implement fire() event in the domain-core package, because app domain module shoot the DomainEvent(s), domain core only creates them.
     */
//    private final DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher;

//    @Override
//    public void fire() {
//        paymentCompletedEventDomainEventPublisher.publish(this);
//    }
}
