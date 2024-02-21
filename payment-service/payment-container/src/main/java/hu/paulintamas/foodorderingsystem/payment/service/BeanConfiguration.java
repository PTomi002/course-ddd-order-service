package hu.paulintamas.foodorderingsystem.payment.service;

import hu.paulintamas.foodorderingsystem.payment.service.domain.PaymentDomainService;
import hu.paulintamas.foodorderingsystem.payment.service.domain.PaymentDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public PaymentDomainService paymentDomainService() {
        return new PaymentDomainServiceImpl();
    }
}
