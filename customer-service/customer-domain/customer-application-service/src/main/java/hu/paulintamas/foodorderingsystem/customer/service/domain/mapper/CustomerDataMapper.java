package hu.paulintamas.foodorderingsystem.customer.service.domain.mapper;

import hu.paulintamas.foodorderingsystem.customer.service.domain.create.CreateCustomerCommand;
import hu.paulintamas.foodorderingsystem.customer.service.domain.create.CreateCustomerResponse;
import hu.paulintamas.foodorderingsystem.customer.service.domain.entity.Customer;
import hu.paulintamas.foodorderingsystem.domain.valueobject.CustomerId;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataMapper {

    public Customer createCustomerCommandToCustomer(CreateCustomerCommand createCustomerCommand) {
        return Customer.builder()
                .id(CustomerId.builder().value(createCustomerCommand.getCustomerId()).build())
                .lastName(createCustomerCommand.getLastName())
                .firstName(createCustomerCommand.getFirstName())
                .username(createCustomerCommand.getUsername())
                .build();
    }

    public CreateCustomerResponse customerToCreateCustomerResponse(Customer customer, String message) {
        return new CreateCustomerResponse(customer.getId().getValue(), message);
    }
}
