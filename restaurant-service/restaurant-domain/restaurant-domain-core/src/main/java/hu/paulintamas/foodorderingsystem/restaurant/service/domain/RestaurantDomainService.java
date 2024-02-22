package hu.paulintamas.foodorderingsystem.restaurant.service.domain;

import hu.paulintamas.foodorderingsystem.domain.event.publisher.DomainEventPublisher;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity.Restaurant;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.event.OrderApprovalEvent;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.event.OrderApprovedEvent;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.event.OrderRejectedEvent;

import java.util.List;

public interface RestaurantDomainService {
    OrderApprovalEvent validateOrder(
            Restaurant restaurant,
            List<String> failureMessages,
            DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher,
            DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher
    );
}
