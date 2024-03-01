package hu.paulintamas.foodorderingsystem.service.domain;

import hu.paulintamas.foodorderingsystem.outbox.OutboxStatus;
import hu.paulintamas.foodorderingsystem.service.domain.dto.create.CreateOrderCommand;
import hu.paulintamas.foodorderingsystem.service.domain.dto.create.CreateOrderResponse;
import hu.paulintamas.foodorderingsystem.service.domain.mapper.OrderDataMapper;
import hu.paulintamas.foodorderingsystem.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
class OrderCreateCommandHandler {

    private final OrderCreateHelper orderCreateHelper;
    private final OrderDataMapper orderDataMapper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final OrderSagaHelper orderSagaHelper;
/*
    [KNOWLEDGE-OUTBOX]
    We dont need them as Outbox Table + Scheduler will handle the event publishing.
    private final OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher;
*/

    /**
     * [KNOWLEDGE]
     * <p>
     * Saving to the DB and firing an event should be atomic.
     * <p>
     * Using patterns: Saga and Outbox
     */
    // now have to use @Transactional here because I want to save multiple objects in a single ACID transaction:
    // - save the Order
    // - save the Outbox Message
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        var orderCreatedDomainEvent = orderCreateHelper.persistOrder(createOrderCommand);
        log.info("Order is created with id: {}", orderCreatedDomainEvent.getOrder().getId().getValue());

        var createOrderResponse = orderDataMapper.orderToCreateOrderResponse(orderCreatedDomainEvent.getOrder(), "Order Created Successfully");
        paymentOutboxHelper.savePaymentOutboxMessage(
                orderDataMapper.orderCreatedEventToOrderPaymentEventPayload(orderCreatedDomainEvent),
                orderCreatedDomainEvent.getOrder().getOrderStatus(),
                orderSagaHelper.orderStatusToSagaStatus(orderCreatedDomainEvent.getOrder().getOrderStatus()),
                OutboxStatus.STARTED,
                // this is where the saga flow starts
                UUID.randomUUID()
        );

        log.info("Returning orderSagaHelper with order id: {}", orderCreatedDomainEvent.getOrder().getId());
        return createOrderResponse;
    }


}
