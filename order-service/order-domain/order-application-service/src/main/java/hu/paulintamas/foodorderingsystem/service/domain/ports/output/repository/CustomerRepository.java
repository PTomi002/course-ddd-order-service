package hu.paulintamas.foodorderingsystem.service.domain.ports.output.repository;

import hu.paulintamas.foodorderingsystem.service.domain.entity.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Optional<Customer> findCustomer(UUID customer);

    Customer save(Customer customer);
}
