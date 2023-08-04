package hu.paulintamas.foodorderingsystem.service.domain;

import hu.paulintamas.foodorderingsystem.service.domain.entity.Order;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Restaurant;
import hu.paulintamas.foodorderingsystem.service.domain.event.OrderCancelledEvent;
import hu.paulintamas.foodorderingsystem.service.domain.event.OrderCreatedEvent;
import hu.paulintamas.foodorderingsystem.service.domain.event.OrderPaidEvent;

import java.util.List;

/**
 * [KNOWLEDGE]
 * <p>
 * Event firing will happen in the application layer (domain layer should not know how to fire).
 * <p>
 * DomainEvents should be created in the domain core.
 * <p>
 * Application layer should not call directly the domain models instead the services.
 */
public interface OrderDomainService {
    OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant);

    OrderPaidEvent payOrder(Order order);

    void approveOrder(Order order);

    OrderCancelledEvent cancelOrderPayments(Order order, List<String> failureMessages);

    void cancelOrder(Order order, List<String> failureMessages);
}
