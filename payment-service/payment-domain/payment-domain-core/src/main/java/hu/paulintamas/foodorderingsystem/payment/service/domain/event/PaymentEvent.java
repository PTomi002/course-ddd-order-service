package hu.paulintamas.foodorderingsystem.payment.service.domain.event;

import hu.paulintamas.foodorderingsystem.domain.event.DomainEvent;
import hu.paulintamas.foodorderingsystem.payment.service.domain.entity.Payment;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@SuperBuilder
@FieldDefaults(makeFinal = true, level = PRIVATE)
public abstract class PaymentEvent implements DomainEvent<Payment> {
    private final Payment payment;
    private final ZonedDateTime zonedDateTime;
    private final List<String> failureMessages;
}
