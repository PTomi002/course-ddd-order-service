package hu.paulintamas.foodorderingsystem.service.domain;

import hu.paulintamas.foodorderingsystem.domain.event.publisher.DomainEventPublisher;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Order;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Restaurant;
import hu.paulintamas.foodorderingsystem.service.domain.event.OrderCancelledEvent;
import hu.paulintamas.foodorderingsystem.service.domain.event.OrderCreatedEvent;
import hu.paulintamas.foodorderingsystem.service.domain.event.OrderPaidEvent;
import hu.paulintamas.foodorderingsystem.service.domain.exception.OrderDomainException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * [KNOWLEDGE]
 * Anemic vs Rich Domain Model
 * TBD.
 */
@RequiredArgsConstructor
public class OrderDomainServiceImpl implements OrderDomainService {

    private static final Logger logger = LoggerFactory.getLogger(OrderDomainServiceImpl.class);

    private final TimeProviderService timeProviderService;

    @Override
    public OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant, DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher) {
        validateRestaurant(restaurant);
        setOrderProductInformation(order, restaurant);
        order.validateOrder();
        order.initializeOrder();
        logger.info("Order with id: {} is initiated", order.getId().getValue());
        return OrderCreatedEvent.builder()
                .order(order)
                .ceratedAt(timeProviderService.now())
                .orderCreatedEventDomainEventPublisher(orderCreatedEventDomainEventPublisher)
                .build();
    }

    @Override
    public OrderPaidEvent payOrder(Order order, DomainEventPublisher<OrderPaidEvent> orderPaidEventDomainEventPublisher) {
        order.pay();
        logger.info("Order with id: {} is paid", order.getId().getValue());
        return OrderPaidEvent.builder()
                .order(order)
                .ceratedAt(timeProviderService.now())
                .orderPaidEventDomainEventPublisher(orderPaidEventDomainEventPublisher)
                .build();
    }

    @Override
    public void approveOrder(Order order) {
        order.approve();
        logger.info("Order with id: {} is approved", order.getId().getValue());
    }

    @Override
    public OrderCancelledEvent cancelOrderPayments(final Order order, final List<String> failureMessages, DomainEventPublisher<OrderCancelledEvent> orderCancelledEventDomainEventPublisher) {
        order.initCancel(failureMessages);
        logger.info("Order with id: {} is being cancelled", order.getId().getValue());
        return OrderCancelledEvent.builder()
                .order(order)
                .ceratedAt(timeProviderService.now())
                .orderCancelledEventDomainEventPublisher(orderCancelledEventDomainEventPublisher)
                .build();
    }

    @Override
    public void cancelOrder(final Order order, final List<String> failureMessages) {
        order.cancel(failureMessages);
        logger.info("Order with id: {} is cancelled", order.getId().getValue());
    }

    private void validateRestaurant(final Restaurant restaurant) {
        if (!restaurant.isActive()) {
            throw new OrderDomainException("Restaurant with id: " + restaurant.getId().getValue() + " is inactive!");
        }
    }

    private void setOrderProductInformation(final Order order, final Restaurant restaurant) {
        order.getOrderItems().forEach(orderItem ->
                restaurant.getProducts().forEach(restaurantProduct -> {
                    var orderProduct = orderItem.getProduct();
                    if (orderProduct.equals(restaurantProduct)) {
                        orderProduct.updateWithConfirmedNameAndPrice(restaurantProduct.getName(), restaurantProduct.getPrice());
                    }
                })
        );
    }

}
