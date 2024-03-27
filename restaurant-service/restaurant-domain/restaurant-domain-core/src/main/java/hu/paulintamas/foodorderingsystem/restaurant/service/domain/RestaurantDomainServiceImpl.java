package hu.paulintamas.foodorderingsystem.restaurant.service.domain;

import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderApprovalStatus;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity.Restaurant;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.event.OrderApprovalEvent;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.event.OrderApprovedEvent;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.event.OrderRejectedEvent;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
public class RestaurantDomainServiceImpl implements RestaurantDomainService{
    @Override
    public OrderApprovalEvent validateOrder(Restaurant restaurant, List<String> failureMessages) {
        restaurant.validateOrder(failureMessages);
        log.info(
                "Validating order with id: {}",
                restaurant.getOrderDetail().getId().getValue()
        );
        if (failureMessages.isEmpty()) {
            log.info(
                    "Order is approved for order id: {}",
                    restaurant.getOrderDetail().getId().getValue()
            );
            restaurant.constructOrderApproval(OrderApprovalStatus.APPROVED);
            return OrderApprovedEvent.builder()
                    .orderApproval(restaurant.getOrderApproval())
                    .restaurantId(restaurant.getId())
                    .failureMessages(List.of())
                    .createdAt(ZonedDateTime.now())
                    .build();
        } else {
            log.info(
                    "Order is rejected for order id: {}",
                    restaurant.getOrderDetail().getId().getValue()
            );
            restaurant.constructOrderApproval(OrderApprovalStatus.REJECTED);
            return OrderRejectedEvent.builder()
                    .orderApproval(restaurant.getOrderApproval())
                    .restaurantId(restaurant.getId())
                    .failureMessages(failureMessages)
                    .createdAt(ZonedDateTime.now())
                    .build();
        }
    }
}
