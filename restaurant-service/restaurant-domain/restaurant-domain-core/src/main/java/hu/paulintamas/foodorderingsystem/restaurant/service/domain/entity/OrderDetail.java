package hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity;

import hu.paulintamas.foodorderingsystem.domain.entity.BaseEntity;
import hu.paulintamas.foodorderingsystem.domain.valueobject.Money;
import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderId;
import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
public class OrderDetail extends BaseEntity<OrderId> {

    private OrderStatus orderStatus;
    private Money totalAmount;
    private final List<Product> products;
}
