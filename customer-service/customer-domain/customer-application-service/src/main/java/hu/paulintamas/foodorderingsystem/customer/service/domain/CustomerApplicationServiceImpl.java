package hu.paulintamas.foodorderingsystem.customer.service.domain;


import hu.paulintamas.foodorderingsystem.customer.service.domain.create.CreateCustomerCommand;
import hu.paulintamas.foodorderingsystem.customer.service.domain.create.CreateCustomerResponse;
import hu.paulintamas.foodorderingsystem.customer.service.domain.mapper.CustomerDataMapper;
import hu.paulintamas.foodorderingsystem.customer.service.domain.ports.input.service.CustomerApplicationService;
import hu.paulintamas.foodorderingsystem.customer.service.domain.ports.output.message.publisher.CustomerMessagePublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
@AllArgsConstructor
class CustomerApplicationServiceImpl implements CustomerApplicationService {

    private final CustomerCreateCommandHandler customerCreateCommandHandler;

    private final CustomerDataMapper customerDataMapper;

    private final CustomerMessagePublisher customerMessagePublisher;

    @Override
    public CreateCustomerResponse createCustomer(CreateCustomerCommand createCustomerCommand) {
        var customerCreatedEvent = customerCreateCommandHandler.createCustomer(createCustomerCommand);
        // [KNOWLEDGE] CQRS
        // Publish customer domain event to event bus.
        customerMessagePublisher.publish(customerCreatedEvent);
        return customerDataMapper.customerToCreateCustomerResponse(customerCreatedEvent.getCustomer(), "Customer saved successfully!");
    }
}
