package hu.paulintamas.foodorderingsystem.restaurant.service.domain.exception;

import hu.paulintamas.foodorderingsystem.domain.exception.DomainException;
import lombok.experimental.StandardException;

@StandardException
public class RestaurantNotFoundException extends DomainException {
}
