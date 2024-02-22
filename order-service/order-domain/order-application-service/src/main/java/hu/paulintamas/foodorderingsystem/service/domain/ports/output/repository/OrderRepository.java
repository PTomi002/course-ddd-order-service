package hu.paulintamas.foodorderingsystem.service.domain.ports.output.repository;

import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderId;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Order;
import hu.paulintamas.foodorderingsystem.service.domain.valueobject.TrackingId;

import java.util.Optional;

/**
 * [KNOWLEDGE]
 * <p>
 * Passing the domain object down to the infra layer, its responsibility will be to map and CRUD it.
 */
public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findByTrackingId(TrackingId trackingId);

    Optional<Order> findyOrderId(OrderId orderId);
}
