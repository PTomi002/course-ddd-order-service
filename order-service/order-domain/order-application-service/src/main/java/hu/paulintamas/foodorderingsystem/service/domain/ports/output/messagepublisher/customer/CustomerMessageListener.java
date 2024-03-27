package hu.paulintamas.foodorderingsystem.service.domain.ports.output.messagepublisher.customer;

import hu.paulintamas.foodorderingsystem.service.domain.dto.message.CustomerModel;

public interface CustomerMessageListener {
    void customerCreated(CustomerModel customerModel);
}
