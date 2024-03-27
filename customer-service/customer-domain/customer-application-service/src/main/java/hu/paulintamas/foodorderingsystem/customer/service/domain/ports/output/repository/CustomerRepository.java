package hu.paulintamas.foodorderingsystem.customer.service.domain.ports.output.repository;

import hu.paulintamas.foodorderingsystem.customer.service.domain.entity.Customer;

public interface CustomerRepository {

    Customer createCustomer(Customer customer);
}
