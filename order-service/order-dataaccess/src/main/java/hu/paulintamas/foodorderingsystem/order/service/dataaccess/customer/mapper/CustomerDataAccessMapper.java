package hu.paulintamas.foodorderingsystem.order.service.dataaccess.customer.mapper;

import hu.paulintamas.foodorderingsystem.domain.valueobject.CustomerId;
import hu.paulintamas.foodorderingsystem.order.service.dataaccess.customer.entity.CustomerEntity;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccessMapper {
    public Customer customerEntityToCustomer(CustomerEntity customerEntity) {
        return Customer.builder()
                .id(CustomerId.builder().value(customerEntity.getId()).build())
                .build();
    }

    public CustomerEntity customerToCustomerEntity(Customer customer) {
        return CustomerEntity.builder()
                .username(customer.getUsername())
                .lastName(customer.getLastName())
                .firstName(customer.getFirstName())
                .id(customer.getId().getValue())
                .build();
    }

}
