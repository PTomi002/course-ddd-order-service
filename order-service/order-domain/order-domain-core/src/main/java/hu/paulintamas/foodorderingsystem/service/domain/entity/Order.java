package hu.paulintamas.foodorderingsystem.service.domain.entity;

import hu.paulintamas.foodorderingsystem.domain.entity.AggregateRoot;
import hu.paulintamas.foodorderingsystem.domain.valueobject.*;
import hu.paulintamas.foodorderingsystem.service.domain.exception.OrderDomainException;
import hu.paulintamas.foodorderingsystem.service.domain.valueobject.OrderItemId;
import hu.paulintamas.foodorderingsystem.service.domain.valueobject.StreetAddress;
import hu.paulintamas.foodorderingsystem.service.domain.valueobject.TrackingId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
public class Order extends AggregateRoot<OrderId> {
    private CustomerId customerId;
    private RestaurantId restaurantId;
    private StreetAddress streetAddress;
    private Money price;
    private List<OrderItem> orderItems;
    private TrackingId trackingId;
    private OrderStatus orderStatus;
    private List<String> errors;

    public void initializeOrder() {
        setId(OrderId.builder().value(UUID.randomUUID()).build());
        setTrackingId(TrackingId.builder().value(UUID.randomUUID()).build());
        setOrderStatus(OrderStatus.PENDING);
        initializeOrderItems();
    }

    // policy checking
    public void validateOrder() {
        validateInitialOrder();
        validateTotalPrice();
        validateOrderItemsPrice();
    }

    public void pay() {
        if (OrderStatus.PENDING != orderStatus) {
            throw new OrderDomainException("order is not in the correct state for pay operation:  " + orderStatus + "!");
        }
        orderStatus = OrderStatus.APPROVED;
    }

    public void approve() {
        if (OrderStatus.APPROVED != orderStatus) {
            throw new OrderDomainException("order is not in the correct state for approve operation:  " + orderStatus + "!");
        }
        orderStatus = OrderStatus.APPROVED;
    }

    public void initCancel(final List<String> failureMessages) {
        if (OrderStatus.APPROVED != orderStatus) {
            throw new OrderDomainException("order is not in the correct state for init cancel operation:  " + orderStatus + "!");
        }
        orderStatus = OrderStatus.CANCELLING;
        updateFailureMessages(failureMessages);
    }

    public void cancel(final List<String> failureMessages) {
        if (!(OrderStatus.CANCELLING == orderStatus || OrderStatus.PENDING == orderStatus)) {
            throw new OrderDomainException("order is not in the correct state for cancel operation:  " + orderStatus + "!");
        }
        orderStatus = OrderStatus.CANCELLED;
        updateFailureMessages(failureMessages);
    }

    private void updateFailureMessages(final List<String> failureMessages) {
        if (errors != null && failureMessages != null) {
            errors.addAll(failureMessages.stream().filter(message -> !message.isEmpty()).toList());
        }
        if (errors == null) {
            errors = failureMessages;
        }
    }

    private void initializeOrderItems() {
        long itemId = 1;
        for (var orderItem : orderItems) {
            orderItem.initializeOrderItem(getId(), OrderItemId.builder().value(itemId++).build());
        }
    }

    private void validateInitialOrder() {
        if (getOrderStatus() != null || getId() != null) {
            throw new OrderDomainException("order is not in correct state for initialization!");
        }
    }

    private void validateTotalPrice() {
        if (getPrice() == null || !price.isGreaterThanZero()) {
            throw new OrderDomainException("total price must be greater than zero!");
        }
    }

    private void validateOrderItemsPrice() {
        var totalPrice = orderItems
                .stream()
                .map(orderItem -> {
                    validateOrderItemPrice(orderItem);
                    return orderItem.getSubTotal();
                })
                .reduce(Money.ZERO, Money::add);

        if (!getPrice().equals(totalPrice)) {
            throw new OrderDomainException("Total price: " + price.getAmount() + " is not equal to Order items total price: " + totalPrice.getAmount() + "!");
        }
    }

    private void validateOrderItemPrice(final OrderItem orderItem) {
        if (!orderItem.isPriceValid()) {
            throw new OrderDomainException("order item price: " + orderItem.getPrice().getAmount() + " is not valid for product: " + orderItem.getProduct().getId().getValue());
        }
    }

}

