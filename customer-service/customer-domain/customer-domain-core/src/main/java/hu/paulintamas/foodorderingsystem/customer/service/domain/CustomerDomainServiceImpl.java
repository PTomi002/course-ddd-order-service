package hu.paulintamas.foodorderingsystem.customer.service.domain;

import hu.paulintamas.foodorderingsystem.customer.service.domain.entity.Customer;
import hu.paulintamas.foodorderingsystem.customer.service.domain.event.CustomerCreatedEvent;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
public class CustomerDomainServiceImpl implements CustomerDomainService {

    public CustomerCreatedEvent validateAndInitiateCustomer(Customer customer) {
        //Any Business logic required to run for a customer creation
        log.info("Customer with id: {} is initiated", customer.getId().getValue());
        return CustomerCreatedEvent.builder()
                .customer(customer)
                .createdAt(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
    }
}

