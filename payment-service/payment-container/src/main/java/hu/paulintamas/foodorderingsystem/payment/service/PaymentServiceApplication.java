package hu.paulintamas.foodorderingsystem.payment.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "hu.paulintamas.foodorderingsystem.payment.service.dataaccess")
@EntityScan(basePackages = "hu.paulintamas.foodorderingsystem.payment.service.dataaccess")
@SpringBootApplication(scanBasePackages = "hu.paulintamas.foodorderingsystem")
public class PaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
