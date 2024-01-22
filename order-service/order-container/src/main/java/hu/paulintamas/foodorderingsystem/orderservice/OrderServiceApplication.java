package hu.paulintamas.foodorderingsystem.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "hu.paulintamas.foodorderingsystem.order.service.dataaccess")
@EntityScan(basePackages = "hu.paulintamas.foodorderingsystem.order.service.dataaccess")
@SpringBootApplication(scanBasePackages = "hu.paulintamas.foodorderingsystem")
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
