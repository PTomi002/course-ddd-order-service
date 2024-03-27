package hu.paulintamas.foodorderingsystem.service.domain;

import hu.paulintamas.foodorderingsystem.service.domain.dto.message.CustomerModel;
import hu.paulintamas.foodorderingsystem.service.domain.exception.OrderDomainException;
import hu.paulintamas.foodorderingsystem.service.domain.mapper.OrderDataMapper;
import hu.paulintamas.foodorderingsystem.service.domain.ports.input.messagelistener.customer.CustomerMessageListener;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CustomerMessageListenerImpl implements CustomerMessageListener {
    private final CustomerRepository customerRepository;
    private final OrderDataMapper orderDataMapper;
    @Override
    public void customerCreated(CustomerModel customerModel) {
        var customer = customerRepository.save(orderDataMapper.customerModelToCustomer(customerModel));
        if (customer == null) {
            log.error(
                    "Customer could not be created in order database with id: {}",
                    customerModel.getId()
            );
            throw new OrderDomainException( "Customer could not be created in order database with id: " + customerModel.getId());
        }
        log.info(
                "Customer is created in order database with id: {}",
                customerModel.getId()
        );
    }
}
