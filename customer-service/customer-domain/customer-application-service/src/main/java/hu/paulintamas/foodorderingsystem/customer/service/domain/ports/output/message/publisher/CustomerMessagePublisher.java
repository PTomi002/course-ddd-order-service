package hu.paulintamas.foodorderingsystem.customer.service.domain.ports.output.message.publisher;

import hu.paulintamas.foodorderingsystem.customer.service.domain.event.CustomerCreatedEvent;

public interface CustomerMessagePublisher {

    void publish(CustomerCreatedEvent customerCreatedEvent);

}