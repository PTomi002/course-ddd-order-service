package hu.paulintamas.foodorderingsystem.service.domain;

import hu.paulintamas.foodorderingsystem.service.domain.dto.create.CreateOrderCommand;
import hu.paulintamas.foodorderingsystem.service.domain.dto.create.CreateOrderResponse;
import hu.paulintamas.foodorderingsystem.service.domain.mapper.OrderDataMapper;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.messagepublisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class OrderCreateCommandHandler {

    private final OrderCreateHelper orderCreateHelper;
    private final OrderDataMapper orderDataMapper;
    private final OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher;

    /**
     * [KNOWLEDGE]
     * <p>
     * Saving to the DB and firing an event should be atomic.
     * <p>
     * Using patterns: Saga and Outbox
     */
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        var orderCreatedDomainEvent = orderCreateHelper.persistOrder(createOrderCommand);
        log.info("Order is created with id: {}", orderCreatedDomainEvent.getOrder().getId().getValue());

        orderCreatedPaymentRequestMessagePublisher.publish(orderCreatedDomainEvent);
        return orderDataMapper.orderToCreateOrderResponse(orderCreatedDomainEvent.getOrder(), "Order Created Successfully");
    }


}
