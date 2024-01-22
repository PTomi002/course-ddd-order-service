package hu.paulintamas.foodorderingsystem.orderservice;

import hu.paulintamas.foodorderingsystem.service.domain.OrderDomainService;
import hu.paulintamas.foodorderingsystem.service.domain.OrderDomainServiceImpl;
import hu.paulintamas.foodorderingsystem.service.domain.TimeProviderService;
import hu.paulintamas.foodorderingsystem.service.domain.TimeProviderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public OrderDomainService orderDomainService(TimeProviderService timeProviderService) {
        return new OrderDomainServiceImpl(timeProviderService);
    }

    @Bean
    public TimeProviderService timeProviderService() {
        return new TimeProviderServiceImpl();
    }

}
