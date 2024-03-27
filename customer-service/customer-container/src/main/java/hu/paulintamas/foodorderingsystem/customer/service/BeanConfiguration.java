package hu.paulintamas.foodorderingsystem.customer.service;

import hu.paulintamas.foodorderingsystem.customer.service.domain.CustomerDomainService;
import hu.paulintamas.foodorderingsystem.customer.service.domain.CustomerDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public CustomerDomainService customerDomainService() {
        return new CustomerDomainServiceImpl();
    }
}
