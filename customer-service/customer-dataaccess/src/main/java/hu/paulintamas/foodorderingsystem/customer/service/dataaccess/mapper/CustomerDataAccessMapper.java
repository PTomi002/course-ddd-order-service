package hu.paulintamas.foodorderingsystem.customer.service.dataaccess.mapper;

import hu.paulintamas.foodorderingsystem.customer.service.dataaccess.entity.CustomerEntity;
import hu.paulintamas.foodorderingsystem.customer.service.domain.entity.Customer;
import hu.paulintamas.foodorderingsystem.domain.valueobject.CustomerId;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccessMapper {

    public Customer customerEntityToCustomer(CustomerEntity customerEntity) {
        return Customer.builder()
                .id(CustomerId.builder().value(customerEntity.getId()).build())
                .lastName(customerEntity.getLastName())
                .username(customerEntity.getUsername())
                .firstName(customerEntity.getFirstName())
                .build();
    }

    public CustomerEntity customerToCustomerEntity(Customer customer) {
        return CustomerEntity.builder()
                .id(customer.getId().getValue())
                .username(customer.getUsername())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .build();
    }

}
