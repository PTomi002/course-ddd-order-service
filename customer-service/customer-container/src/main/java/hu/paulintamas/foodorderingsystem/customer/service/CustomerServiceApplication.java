package hu.paulintamas.foodorderingsystem.customer.service;

import hu.paulintamas.foodorderingsystem.kafka.config.data.KafkaConsumerConfigData;
import hu.paulintamas.foodorderingsystem.kafka.consumer.KafkaConsumerConfig;
import hu.paulintamas.foodorderingsystem.kafka.consumer.service.KafkaConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {
        "hu.paulintamas.foodorderingsystem.customer.service.dataaccess",
        "hu.paulintamas.foodorderingsystem.dataaccess"
})
@EntityScan(basePackages = {
        "hu.paulintamas.foodorderingsystem.customer.service.dataaccess",
        "hu.paulintamas.foodorderingsystem.dataaccess"
})
@ComponentScan(
        basePackages = "hu.paulintamas.foodorderingsystem",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = KafkaConsumerConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = KafkaConsumerConfigData.class)
        }
)
@SpringBootApplication
public class CustomerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}
