package hu.paulintamas.foodorderingsystem.restaurant.service;

import hu.paulintamas.foodorderingsystem.restaurant.service.domain.RestaurantDomainService;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.RestaurantDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public RestaurantDomainService paymentDomainService() {
        return new RestaurantDomainServiceImpl();
    }
}
