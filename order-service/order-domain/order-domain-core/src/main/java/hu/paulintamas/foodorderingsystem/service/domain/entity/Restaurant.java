package hu.paulintamas.foodorderingsystem.service.domain.entity;

import hu.paulintamas.foodorderingsystem.domain.entity.AggregateRoot;
import hu.paulintamas.foodorderingsystem.domain.valueobject.RestaurantId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
public class Restaurant extends AggregateRoot<RestaurantId> {
    List<Product> products;
    boolean active;
}
