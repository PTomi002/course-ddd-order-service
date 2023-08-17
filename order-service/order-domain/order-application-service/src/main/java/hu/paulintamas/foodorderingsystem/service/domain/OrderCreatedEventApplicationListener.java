package hu.paulintamas.foodorderingsystem.service.domain;

import hu.paulintamas.foodorderingsystem.service.domain.event.OrderCreatedEvent;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.messagepublisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@AllArgsConstructor
public class OrderCreatedEventApplicationListener {
    private final OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher;

    /**
     * Will be triggered after the caller transaction successfully completed.
     */
    @TransactionalEventListener
    void process(OrderCreatedEvent orderCreatedEvent) {

    }
}
