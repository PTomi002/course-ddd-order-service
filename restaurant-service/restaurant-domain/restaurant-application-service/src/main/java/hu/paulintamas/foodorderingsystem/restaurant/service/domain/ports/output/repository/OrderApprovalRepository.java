package hu.paulintamas.foodorderingsystem.restaurant.service.domain.ports.output.repository;

import hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity.OrderApproval;

public interface OrderApprovalRepository {
    OrderApproval save(OrderApproval orderApproval);
}
