package hu.paulintamas.foodorderingsystem.order.service.dataaccess.order.adapter;

import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderId;
import hu.paulintamas.foodorderingsystem.order.service.dataaccess.order.mapper.OrderDataAccessMapper;
import hu.paulintamas.foodorderingsystem.order.service.dataaccess.order.repository.OrderJpaRepository;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Order;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.repository.OrderRepository;
import hu.paulintamas.foodorderingsystem.service.domain.valueobject.TrackingId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderDataAccessMapper orderDataAccessMapper;

    @Override
    public Order save(Order order) {
        var oderEntity = orderDataAccessMapper.orderToOrderEntity(order);
        return orderDataAccessMapper.orderEntityToOrder(
                orderJpaRepository.save(oderEntity)
        );
    }

    @Override
    public Optional<Order> findByTrackingId(TrackingId trackingId) {
        return orderJpaRepository
                .findByTrackingId(trackingId.getValue())
                .map(orderDataAccessMapper::orderEntityToOrder);
    }

    @Override
    public Optional<Order> findyOrderId(OrderId orderId) {
        return orderJpaRepository
                .findById(orderId.getValue())
                .map(orderDataAccessMapper::orderEntityToOrder);
    }
}
