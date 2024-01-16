package hu.paulintamas.foodorderingsystem.order.service.application.exception.handler;

import hu.paulintamas.foodorderingsystem.application.ErrorDTO;
import hu.paulintamas.foodorderingsystem.application.GlobalExceptionHandler;
import hu.paulintamas.foodorderingsystem.service.domain.exception.OrderDomainException;
import hu.paulintamas.foodorderingsystem.service.domain.exception.OrderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class OrderGlobalExceptionHandler extends GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {OrderDomainException.class})
    public ErrorDTO handleException(OrderDomainException orderDomainException) {
        log.error("Message='" + orderDomainException.getMessage() + "'", orderDomainException);
        return ErrorDTO.builder()
                .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(orderDomainException.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {OrderNotFoundException.class})
    public ErrorDTO handleException(OrderNotFoundException orderDomainException) {
        log.error("Message='" + orderDomainException.getMessage() + "'", orderDomainException);
        return ErrorDTO.builder()
                .code(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(orderDomainException.getMessage())
                .build();
    }

}
