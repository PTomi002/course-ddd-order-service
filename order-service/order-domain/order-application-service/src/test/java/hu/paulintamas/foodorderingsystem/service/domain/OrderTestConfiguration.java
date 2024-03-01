package hu.paulintamas.foodorderingsystem.service.domain;

import hu.paulintamas.foodorderingsystem.service.domain.ports.output.messagepublisher.payment.PaymentRequestMessagePublisher;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.messagepublisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.repository.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

/**
 * Input / Output Ports are mocked here as their implementations are in a different gradle module.
 */
@SpringBootApplication(scanBasePackages = "hu.paulintamas.foodorderingsystem.service.domain")
public class OrderTestConfiguration {

    @Bean
    public PaymentRequestMessagePublisher paymentRequestMessagePublisher() {
        return mock(PaymentRequestMessagePublisher.class);
    }

    @Bean
    public RestaurantApprovalRequestMessagePublisher restaurantApprovalRequestMessagePublisher() {
        return mock(RestaurantApprovalRequestMessagePublisher.class);
    }

    @Bean
    public PaymentOutboxRepository paymentOutboxRepository() {
        return mock(PaymentOutboxRepository.class);
    }

    @Bean
    public ApprovalOutboxRepository approvalOutboxRepository() {
        return mock(ApprovalOutboxRepository.class);
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
