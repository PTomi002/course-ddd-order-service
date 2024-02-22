package hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity;

import hu.paulintamas.foodorderingsystem.domain.entity.BaseEntity;
import hu.paulintamas.foodorderingsystem.domain.valueobject.Money;
import hu.paulintamas.foodorderingsystem.domain.valueobject.ProductId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
public class Product extends BaseEntity<ProductId> {
    private String name;
    private Money price;
    private int quantity;
    private boolean available;

    public void updateWithConfirmedNamePriceAndAvailableFields(
            String name,
            Money price,
            boolean available
    ) {
        this.name = name;
        this.price = price;
        this.available = available;
    }
}
