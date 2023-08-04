package hu.paulintamas.foodorderingsystem.service.domain.entity;

import hu.paulintamas.foodorderingsystem.domain.entity.BaseEntity;
import hu.paulintamas.foodorderingsystem.domain.valueobject.Money;
import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderId;
import hu.paulintamas.foodorderingsystem.service.domain.valueobject.OrderItemId;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
public class OrderItem extends BaseEntity<OrderItemId> {
    private OrderId orderId;
    private Product product;
    private int quantity;
    private Money price;
    private Money subTotal;

    void initializeOrderItem(final OrderId orderId, final OrderItemId orderItemId) {
        setOrderId(orderId);
        super.setId(orderItemId);
    }

    boolean isPriceValid() {
        return price.isGreaterThanZero()
                && price.equals(product.getPrice())
                && price.multiply(quantity).equals(subTotal);
    }

}
