package hu.paulintamas.foodorderingsystem.customer.service.domain.event;

import hu.paulintamas.foodorderingsystem.customer.service.domain.entity.Customer;
import hu.paulintamas.foodorderingsystem.domain.event.DomainEvent;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

import static lombok.AccessLevel.PRIVATE;

@Getter
@SuperBuilder
@ToString(callSuper = true)
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class CustomerCreatedEvent implements DomainEvent<Customer> {

    private final Customer customer;

    private final ZonedDateTime createdAt;
}
