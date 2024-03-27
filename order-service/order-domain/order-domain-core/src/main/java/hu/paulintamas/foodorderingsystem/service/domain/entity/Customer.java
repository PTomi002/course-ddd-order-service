package hu.paulintamas.foodorderingsystem.service.domain.entity;

import hu.paulintamas.foodorderingsystem.domain.entity.AggregateRoot;
import hu.paulintamas.foodorderingsystem.domain.valueobject.CustomerId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

// [KNOWLEDGE] CQRS
// Read model of customer.
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
public class Customer extends AggregateRoot<CustomerId> {
    private final String username;
    private final String firstName;
    private final String lastName;
}
