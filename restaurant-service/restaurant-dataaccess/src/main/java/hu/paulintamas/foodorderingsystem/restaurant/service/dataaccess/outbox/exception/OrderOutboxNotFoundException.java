package hu.paulintamas.foodorderingsystem.restaurant.service.dataaccess.outbox.exception;

import lombok.experimental.StandardException;

@StandardException
public class OrderOutboxNotFoundException extends RuntimeException {
}
