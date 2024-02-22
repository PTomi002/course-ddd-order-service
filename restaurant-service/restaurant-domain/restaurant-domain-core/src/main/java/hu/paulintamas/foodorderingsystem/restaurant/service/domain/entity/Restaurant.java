package hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity;

import hu.paulintamas.foodorderingsystem.domain.entity.AggregateRoot;
import hu.paulintamas.foodorderingsystem.domain.valueobject.*;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.valueobject.OrderApprovalId;
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
public class Restaurant extends AggregateRoot<RestaurantId> {
    private OrderApproval orderApproval;
    private boolean active;
    private final OrderDetail orderDetail;

    public void validateOrder(List<String> failureMessages) {
        /**
         * [KNOWLEDGE]
         * We are collecting the errors instead of throwing them,
         * as we dont want to go outside of the domain to handle valid business
         * error routes e.g.: invalid payment or restaurant.
         */
        if (orderDetail.getOrderStatus() != OrderStatus.PAID) {
            failureMessages.add("Payment is not complete for order: " + orderDetail.getId());
        }
        var totalAmount = orderDetail.getProducts().
                stream()
                .map(product -> {
                    if (!product.isAvailable()) {
                        failureMessages.add("Product with id: " + product.getId().getValue() + " is not available.");
                    }
                    return product.getPrice().multiply(product.getQuantity());
                })
                .reduce(Money.ZERO, Money::add);
        if (!totalAmount.equals(orderDetail.getTotalAmount())) {
            failureMessages.add("Price total is not correct for order: " + orderDetail.getId());
        }
    }

    public void constructOrderApproval(OrderApprovalStatus orderApprovalStatus) {
        this.orderApproval = OrderApproval.builder()
                .id(OrderApprovalId.builder().id(UUID.randomUUID()).build())
                .orderId(this.getOrderDetail().getId())
                .restaurantId(this.getId())
                .orderApprovalStatus(orderApprovalStatus)
                .build();
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
