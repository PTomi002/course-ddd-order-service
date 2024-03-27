package hu.paulintamas.foodorderingsystem.payment.service.messaging.mapper;

import hu.paulintamas.foodorderingsystem.domain.valueobject.PaymentOrderStatus;
import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.PaymentRequestAvroModel;
import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.PaymentResponseAvroModel;
import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.PaymentStatus;
import hu.paulintamas.foodorderingsystem.payment.service.domain.dto.PaymentRequest;
import hu.paulintamas.foodorderingsystem.payment.service.domain.outbox.model.OrderEventPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMessagingDataMapper {

    public PaymentResponseAvroModel orderEventPayloadToPaymentResponseAvroModel(String sagaId,
                                                                                OrderEventPayload orderEventPayload) {
        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.fromString(sagaId))
                .setPaymentId(UUID.fromString(orderEventPayload.getPaymentId()))
                .setCustomerId(UUID.fromString(orderEventPayload.getCustomerId()))
                .setOrderId(UUID.fromString(orderEventPayload.getOrderId()))
                .setPrice(orderEventPayload.getPrice())
                .setCreatedAt(orderEventPayload.getCreatedAt().toInstant())//??
                .setPaymentStatus(PaymentStatus.valueOf(orderEventPayload.getPaymentStatus()))
                .setFailureMessages(orderEventPayload.getFailureMessages())
                .build();
    }


    public PaymentRequest paymentRequestAvroModelToPaymentRequest(PaymentRequestAvroModel paymentRequestAvroModel) {
        return PaymentRequest.builder()
                .id(paymentRequestAvroModel.getId().toString())
                .sagaId(paymentRequestAvroModel.getSagaId().toString())
                .customerId(paymentRequestAvroModel.getCustomerId().toString())
                .orderId(paymentRequestAvroModel.getOrderId().toString())
                .price(paymentRequestAvroModel.getPrice())
                .createdAt(paymentRequestAvroModel.getCreatedAt())
                .paymentOrderStatus(PaymentOrderStatus.valueOf(paymentRequestAvroModel.getPaymentOrderStatus().name()))
                .build();
    }

}
