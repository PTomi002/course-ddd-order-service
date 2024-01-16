package hu.paulintamas.foodorderingsystem.order.service.dataaccess.customer.adapter;

import hu.paulintamas.foodorderingsystem.order.service.dataaccess.customer.mapper.CustomerDataAccessMapper;
import hu.paulintamas.foodorderingsystem.order.service.dataaccess.customer.repository.CustomerJpaRepository;
import hu.paulintamas.foodorderingsystem.order.service.dataaccess.order.repository.OrderJpaRepository;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Customer;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {
    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerDataAccessMapper customerDataAccessMapper;

    @Override
    public Optional<Customer> findCustomer(UUID customerId) {
        return customerJpaRepository
                .findById(customerId)
                .map(customerDataAccessMapper::customerEntityToCustomer);
    }
}
