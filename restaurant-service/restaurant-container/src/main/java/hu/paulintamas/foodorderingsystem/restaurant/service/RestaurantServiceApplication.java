package hu.paulintamas.foodorderingsystem.restaurant.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {
        "hu.paulintamas.foodorderingsystem.restaurant.service.dataaccess",
        "hu.paulintamas.foodorderingsystem.dataaccess"
})
@EntityScan(basePackages = {
        "hu.paulintamas.foodorderingsystem.restaurant.service.dataaccess",
        "hu.paulintamas.foodorderingsystem.dataaccess"
})
@SpringBootApplication(scanBasePackages = "hu.paulintamas.foodorderingsystem")
public class RestaurantServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceApplication.class, args);
    }
}
