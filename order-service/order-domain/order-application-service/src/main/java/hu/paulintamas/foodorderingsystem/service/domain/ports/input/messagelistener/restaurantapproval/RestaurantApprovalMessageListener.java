package hu.paulintamas.foodorderingsystem.service.domain.ports.input.messagelistener.restaurantapproval;

import hu.paulintamas.foodorderingsystem.service.domain.dto.message.RestaurantApprovalResponse;

public interface RestaurantApprovalMessageListener {
    void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse);

    void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse);
}
