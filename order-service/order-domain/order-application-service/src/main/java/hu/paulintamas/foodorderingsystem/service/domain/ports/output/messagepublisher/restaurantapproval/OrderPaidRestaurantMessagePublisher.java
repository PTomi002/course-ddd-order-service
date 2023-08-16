package hu.paulintamas.foodorderingsystem.service.domain.ports.output.messagepublisher.restaurantapproval;

import hu.paulintamas.foodorderingsystem.domain.event.publisher.DomainEventPublisher;
import hu.paulintamas.foodorderingsystem.service.domain.event.OrderPaidEvent;

public interface OrderPaidRestaurantMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {
}
