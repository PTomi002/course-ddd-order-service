package hu.paulintamas.foodorderingsystem.order.service.messaging.mapper;

import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.PaymentOrderStatus;
import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.PaymentRequestAvroModel;
import hu.paulintamas.foodorderingsystem.service.domain.event.OrderCancelledEvent;
import hu.paulintamas.foodorderingsystem.service.domain.event.OrderCreatedEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderMessagingDataMapper {

    public PaymentRequestAvroModel orderCreatedEventToPaymentRequestAvroModel(OrderCreatedEvent orderCreatedEvent) {
        var order = orderCreatedEvent.getOrder();
        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.randomUUID())
                .setCustomerId(order.getCustomerId().getValue())
                .setOrderId(order.getId().getValue())
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderCreatedEvent.getCeratedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
                .build();
    }

    public PaymentRequestAvroModel orderCancelledEventToPaymentRequestAvroModel(OrderCancelledEvent orderCancelledEvent) {
        var order = orderCancelledEvent.getOrder();
        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.randomUUID())
                .setCustomerId(order.getCustomerId().getValue())
                .setOrderId(order.getId().getValue())
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderCancelledEvent.getCeratedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.CANCELLED)
                .build();
    }

}
