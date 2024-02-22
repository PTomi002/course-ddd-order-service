package hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity;

import hu.paulintamas.foodorderingsystem.domain.entity.BaseEntity;
import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderApprovalStatus;
import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderId;
import hu.paulintamas.foodorderingsystem.domain.valueobject.RestaurantId;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.valueobject.OrderApprovalId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
public class OrderApproval extends BaseEntity<OrderApprovalId> {
    private final RestaurantId restaurantId;
    private final OrderId orderId;
    private final OrderApprovalStatus orderApprovalStatus;
}
