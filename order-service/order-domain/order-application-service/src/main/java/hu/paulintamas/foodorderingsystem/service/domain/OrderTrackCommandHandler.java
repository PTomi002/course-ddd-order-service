package hu.paulintamas.foodorderingsystem.service.domain;

import hu.paulintamas.foodorderingsystem.service.domain.dto.track.TrackOrderQuery;
import hu.paulintamas.foodorderingsystem.service.domain.dto.track.TrackOrderResponse;
import hu.paulintamas.foodorderingsystem.service.domain.exception.OrderNotFoundException;
import hu.paulintamas.foodorderingsystem.service.domain.mapper.OrderDataMapper;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.repository.OrderRepository;
import hu.paulintamas.foodorderingsystem.service.domain.valueobject.TrackingId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@AllArgsConstructor
class OrderTrackCommandHandler {

    private final OrderRepository orderRepository;
    private final OrderDataMapper orderDataMapper;

    @Transactional(readOnly = true)
    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
        var optionalOrder = orderRepository.findByTrackingId(TrackingId.builder().value(trackOrderQuery.getOrderTrackingId()).build());
        if (optionalOrder.isEmpty()) {
            log.warn("Order not found with id: {}", trackOrderQuery.getOrderTrackingId());
            throw new OrderNotFoundException("Order not found with id: " + trackOrderQuery.getOrderTrackingId());
        }
        return orderDataMapper.orderToTrackOrderResponse(optionalOrder.get());
    }
}
