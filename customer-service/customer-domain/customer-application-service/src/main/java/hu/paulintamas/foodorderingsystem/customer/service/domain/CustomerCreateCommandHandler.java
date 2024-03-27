package hu.paulintamas.foodorderingsystem.customer.service.domain;

import hu.paulintamas.foodorderingsystem.customer.service.domain.create.CreateCustomerCommand;
import hu.paulintamas.foodorderingsystem.customer.service.domain.event.CustomerCreatedEvent;
import hu.paulintamas.foodorderingsystem.customer.service.domain.exception.CustomerDomainException;
import hu.paulintamas.foodorderingsystem.customer.service.domain.mapper.CustomerDataMapper;
import hu.paulintamas.foodorderingsystem.customer.service.domain.ports.output.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@AllArgsConstructor
class CustomerCreateCommandHandler {

    private final CustomerDomainService customerDomainService;

    private final CustomerRepository customerRepository;

    private final CustomerDataMapper customerDataMapper;

    @Transactional
    public CustomerCreatedEvent createCustomer(CreateCustomerCommand createCustomerCommand) {
        var customer = customerDataMapper.createCustomerCommandToCustomer(createCustomerCommand);
        var customerCreatedEvent = customerDomainService.validateAndInitiateCustomer(customer);
        var savedCustomer = customerRepository.createCustomer(customer);
        if (savedCustomer == null) {
            log.error("Could not save customer with id: {}", createCustomerCommand.getCustomerId());
            throw new CustomerDomainException("Could not save customer with id " +
                    createCustomerCommand.getCustomerId());
        }
        log.info("Returning CustomerCreatedEvent for customer id: {}", createCustomerCommand.getCustomerId());
        return customerCreatedEvent;
    }
}
