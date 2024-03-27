package hu.paulintamas.foodorderingsystem.payment.service.dataaccess.outbox.exception;

import lombok.experimental.StandardException;

@StandardException
public class OrderOutboxNotFoundException extends RuntimeException {
}
