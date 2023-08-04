package hu.paulintamas.foodorderingsystem.service.domain.event;

import hu.paulintamas.foodorderingsystem.domain.event.DomainEvent;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Order;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

import static lombok.AccessLevel.PRIVATE;

@Getter
@SuperBuilder
@FieldDefaults(makeFinal = true, level = PRIVATE)
public abstract class OrderEvent implements DomainEvent<Order> {
    Order order;
    ZonedDateTime ceratedAt;
}
