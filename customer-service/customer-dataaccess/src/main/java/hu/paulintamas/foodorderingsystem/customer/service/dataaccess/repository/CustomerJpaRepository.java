package hu.paulintamas.foodorderingsystem.customer.service.dataaccess.repository;

import hu.paulintamas.foodorderingsystem.customer.service.dataaccess.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID> {


}
