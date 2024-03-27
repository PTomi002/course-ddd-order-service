package hu.paulintamas.foodorderingsystem.customer.service.dataaccess.adapter;

import hu.paulintamas.foodorderingsystem.customer.service.dataaccess.mapper.CustomerDataAccessMapper;
import hu.paulintamas.foodorderingsystem.customer.service.dataaccess.repository.CustomerJpaRepository;
import hu.paulintamas.foodorderingsystem.customer.service.domain.entity.Customer;
import hu.paulintamas.foodorderingsystem.customer.service.domain.ports.output.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;

    private final CustomerDataAccessMapper customerDataAccessMapper;

    @Override
    public Customer createCustomer(Customer customer) {
        return customerDataAccessMapper.customerEntityToCustomer(
                customerJpaRepository.save(customerDataAccessMapper.customerToCustomerEntity(customer))
        );
    }
}
