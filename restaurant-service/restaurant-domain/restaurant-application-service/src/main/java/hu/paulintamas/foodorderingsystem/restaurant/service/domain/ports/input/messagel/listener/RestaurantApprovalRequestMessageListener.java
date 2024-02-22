package hu.paulintamas.foodorderingsystem.restaurant.service.domain.ports.input.messagel.listener;

import hu.paulintamas.foodorderingsystem.restaurant.service.domain.dto.RestaurantApprovalRequest;

public interface RestaurantApprovalRequestMessageListener {
    void approveOrder(RestaurantApprovalRequest restaurantApprovalRequest);
}
