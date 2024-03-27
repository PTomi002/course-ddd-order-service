package hu.paulintamas.foodorderingsystem.payment.service.dataaccess.outbox.adapter;

import hu.paulintamas.foodorderingsystem.domain.valueobject.PaymentStatus;
import hu.paulintamas.foodorderingsystem.outbox.OutboxStatus;
import hu.paulintamas.foodorderingsystem.payment.service.dataaccess.outbox.exception.OrderOutboxNotFoundException;
import hu.paulintamas.foodorderingsystem.payment.service.dataaccess.outbox.mapper.OrderOutboxDataAccessMapper;
import hu.paulintamas.foodorderingsystem.payment.service.dataaccess.outbox.repository.OrderOutboxJpaRepository;
import hu.paulintamas.foodorderingsystem.payment.service.domain.outbox.model.OrderOutboxMessage;
import hu.paulintamas.foodorderingsystem.payment.service.domain.ports.output.repository.OrderOutboxRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class OrderOutboxRepositoryImpl implements OrderOutboxRepository {

    private final OrderOutboxJpaRepository orderOutboxJpaRepository;
    private final OrderOutboxDataAccessMapper orderOutboxDataAccessMapper;

    @Override
    public OrderOutboxMessage save(OrderOutboxMessage orderPaymentOutboxMessage) {
        return orderOutboxDataAccessMapper.orderOutboxEntityToOrderOutboxMessage(
                orderOutboxJpaRepository.save(
                        orderOutboxDataAccessMapper.orderOutboxMessageToOutboxEntity(orderPaymentOutboxMessage)
                )
        );
    }

    @Override
    public Optional<List<OrderOutboxMessage>> findByTypeAndOutboxStatus(String sagaType, OutboxStatus outboxStatus) {
        return Optional.of(orderOutboxJpaRepository.findByTypeAndOutboxStatus(sagaType, outboxStatus)
                .orElseThrow(() -> new OrderOutboxNotFoundException("Approval outbox object " +
                        "cannot be found for saga type " + sagaType))
                .stream()
                .map(orderOutboxDataAccessMapper::orderOutboxEntityToOrderOutboxMessage)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<OrderOutboxMessage> findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(String sagaType,
                                                                            UUID sagaId,
                                                                            PaymentStatus paymentStatus,
                                                                            OutboxStatus outboxStatus) {
        return orderOutboxJpaRepository
                .findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(sagaType, sagaId, paymentStatus, outboxStatus)
                .map(orderOutboxDataAccessMapper::orderOutboxEntityToOrderOutboxMessage);
    }

    @Override
    public void deleteByTypeAndOutboxStatus(String sagaType, OutboxStatus outboxStatus) {
        orderOutboxJpaRepository.deleteByTypeAndOutboxStatus(sagaType, outboxStatus);
    }
}
