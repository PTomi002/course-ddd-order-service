package hu.paulintamas.foodorderingsystem.service.domain;

import hu.paulintamas.foodorderingsystem.service.domain.ports.output.messagepublisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.messagepublisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.messagepublisher.restaurantapproval.OrderPaidRestaurantMessagePublisher;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.repository.CustomerRepository;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.repository.OrderRepository;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.repository.RestaurantRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

/**
 * Input / Output Ports are mocked here as their implementations are in a different gradle module.
 */
@SpringBootApplication(scanBasePackages = "hu.paulintamas.foodorderingsystem.service.domain")
public class OrderTestConfiguration {

    @Bean
    public OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher() {
        return mock(OrderCreatedPaymentRequestMessagePublisher.class);
    }

    @Bean
    public OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher() {
        return mock(OrderCancelledPaymentRequestMessagePublisher.class);
    }

    @Bean
    public OrderPaidRestaurantMessagePublisher orderPaidRestaurantMessagePublisher() {
        return mock(OrderPaidRestaurantMessagePublisher.class);
    }

    @Bean
    public OrderRepository orderRepository() {
        return mock(OrderRepository.class);
    }

    @Bean
    public CustomerRepository customerRepository() {
        return mock(CustomerRepository.class);
    }

    @Bean
    public RestaurantRepository restaurantRepository() {
        return mock(RestaurantRepository.class);
    }

    @Bean
    public TimeProviderService timeProviderService() {
        return new TimeProviderServiceImpl();
    }

    @Bean
    public OrderDomainService orderDomainService(TimeProviderService timeProviderService) {
        return new OrderDomainServiceImpl(timeProviderService);
    }

}
