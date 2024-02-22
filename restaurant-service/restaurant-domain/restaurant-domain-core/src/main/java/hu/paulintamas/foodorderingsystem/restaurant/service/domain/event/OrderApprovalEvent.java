package hu.paulintamas.foodorderingsystem.restaurant.service.domain.event;

import hu.paulintamas.foodorderingsystem.domain.event.DomainEvent;
import hu.paulintamas.foodorderingsystem.domain.valueobject.RestaurantId;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity.OrderApproval;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@SuperBuilder
@FieldDefaults(makeFinal = true, level = PRIVATE)
public abstract class OrderApprovalEvent implements DomainEvent<OrderApproval> {
    private final OrderApproval orderApproval;
    private final RestaurantId restaurantId;
    private final List<String> failureMessages;
    private final ZonedDateTime createdAt;
}
