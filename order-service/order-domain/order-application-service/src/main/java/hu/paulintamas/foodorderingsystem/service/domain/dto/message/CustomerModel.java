package hu.paulintamas.foodorderingsystem.service.domain.dto.message;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CustomerModel {
    String id;
    String username;
    String firstName;
    String lastName;
}
