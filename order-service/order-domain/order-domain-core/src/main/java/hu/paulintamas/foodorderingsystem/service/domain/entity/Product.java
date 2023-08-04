package hu.paulintamas.foodorderingsystem.service.domain.entity;

import hu.paulintamas.foodorderingsystem.domain.entity.BaseEntity;
import hu.paulintamas.foodorderingsystem.domain.valueobject.Money;
import hu.paulintamas.foodorderingsystem.domain.valueobject.ProductId;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
public class Product extends BaseEntity<ProductId> {
    private String name;
    private Money price;

    public void updateWithConfirmedNameAndPrice(final String name, final Money price) {
        this.name = name;
        this.price = price;
    }
}
