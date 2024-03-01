package hu.paulintamas.foodorderingsystem.service.domain;

import hu.paulintamas.foodorderingsystem.service.domain.dto.create.CreateOrderCommand;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Order;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Restaurant;
import hu.paulintamas.foodorderingsystem.service.domain.event.OrderCreatedEvent;
import hu.paulintamas.foodorderingsystem.service.domain.exception.OrderDomainException;
import hu.paulintamas.foodorderingsystem.service.domain.mapper.OrderDataMapper;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.repository.CustomerRepository;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.repository.OrderRepository;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
class OrderCreateHelper {
    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderDataMapper orderDataMapper;

    @Transactional // by default an inner @Transactional will use the outer/parent @Transactional
    public OrderCreatedEvent persistOrder(CreateOrderCommand createOrderCommand) {
        checkCustomer(createOrderCommand.getCustomerId());

        var restaurant = checkRestaurant(createOrderCommand);
        var order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);

        var orderCreatedDomainEvent = orderDomainService.validateAndInitiateOrder(order, restaurant);
        var savedOrder = saveOrder(order);

        log.info("Order is created with id: {}", savedOrder.getId().getValue());
        return orderCreatedDomainEvent;
    }

    /**
     * [KNOWLEDGE]
     * <p>
     * Just to check the AVAILABILITY of the restaurant it is enough to check it here and not in the domain service layer.
     * <p>
     * Because we dont do any business related logic check.
     */
    private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
        var restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
        var optionalRestaurant = restaurantRepository.findRestaurantInformation(restaurant);

        if (optionalRestaurant.isEmpty()) {
            log.warn("Could not find restaurant with restaurant id: {}", createOrderCommand.getRestaurantId());
            throw new OrderDomainException("Could not find restaurant with restaurant id: " + createOrderCommand.getRestaurantId());
        }
        return optionalRestaurant.get();
    }

    /**
     * Just to check the AVAILABILITY of the customer it is enough to check it here and not in the domain service layer.
     * <p>
     * Because we dont do any business related logic check.
     */
    private void checkCustomer(UUID customerId) {
        var customer = customerRepository.findCustomer(customerId);
        if (customer.isEmpty()) {
            log.warn("Customer not found with customer id: {}", customerId);
            throw new OrderDomainException("Customer not found with customer id: " + customer);
        }
    }

    private Order saveOrder(Order order) {
        var savedOrder = orderRepository.save(order);
        if (savedOrder == null) {
            log.error("Could not save order!");
            throw new OrderDomainException("Could not save order!");
        }
        log.info("Order is saved with id: {}", savedOrder.getId().getValue());
        return savedOrder;
    }

}
