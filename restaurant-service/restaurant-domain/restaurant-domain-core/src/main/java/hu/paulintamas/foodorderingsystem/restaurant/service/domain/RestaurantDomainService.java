package hu.paulintamas.foodorderingsystem.restaurant.service.domain;

import hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity.Restaurant;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.event.OrderApprovalEvent;

import java.util.List;

public interface RestaurantDomainService {
    OrderApprovalEvent validateOrder(
            Restaurant restaurant,
            List<String> failureMessages
    );
}
