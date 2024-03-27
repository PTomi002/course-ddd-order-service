package hu.paulintamas.foodorderingsystem.customer.service.domain.ports.input.service;

import hu.paulintamas.foodorderingsystem.customer.service.domain.create.CreateCustomerCommand;
import hu.paulintamas.foodorderingsystem.customer.service.domain.create.CreateCustomerResponse;
import jakarta.validation.Valid;

public interface CustomerApplicationService {

    CreateCustomerResponse createCustomer(@Valid CreateCustomerCommand createCustomerCommand);

}
