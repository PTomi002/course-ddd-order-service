package hu.paulintamas.foodorderingsystem.order.service.dataaccess.customer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
// [KNOWLEDGE] CQRS
// Right now Customer served via a cross database call through OrderService API, we will break it up with CQRS.
// @Table(name = "order_customer_m_view", schema = "customer")
@Table(name = "customers")
public class CustomerEntity {

    @Id
    private UUID id;
    private String username;
    private String lastName;
    private String firstName;
}
