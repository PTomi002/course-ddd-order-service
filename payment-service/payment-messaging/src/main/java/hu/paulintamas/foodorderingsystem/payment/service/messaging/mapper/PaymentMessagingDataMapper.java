package hu.paulintamas.foodorderingsystem.payment.service.messaging.mapper;

import hu.paulintamas.foodorderingsystem.domain.valueobject.PaymentOrderStatus;
import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.PaymentRequestAvroModel;
import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.PaymentResponseAvroModel;
import hu.paulintamas.foodorderingsystem.kafka.order.avro.model.PaymentStatus;
import hu.paulintamas.foodorderingsystem.payment.service.domain.dto.PaymentRequest;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentCancelledEvent;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentCompletedEvent;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentFailedEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMessagingDataMapper {

    public PaymentResponseAvroModel paymentCompletedEventToPaymentResponseAvroModel(PaymentCompletedEvent paymentCompletedEvent) {
        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.randomUUID())
                .setPaymentId(paymentCompletedEvent.getPayment().getId().getValue())
                .setCustomerId(paymentCompletedEvent.getPayment().getCustomerId().getValue())
                .setOrderId(paymentCompletedEvent.getPayment().getOrderId().getValue())
                .setPrice(paymentCompletedEvent.getPayment().getPrice().getAmount())
                .setCreatedAt(paymentCompletedEvent.getPayment().getCreatedAt().toInstant())
                .setPaymentStatus(PaymentStatus.valueOf(paymentCompletedEvent.getPayment().getPaymentStatus().toString()))
                .setFailureMessages(paymentCompletedEvent.getFailureMessages())
                .build();
    }

    public PaymentResponseAvroModel paymentCancelledEventToPaymentResponseAvroModel(PaymentCancelledEvent paymentCancelledEvent) {
        return PaymentResponseAvroModel.newBuilder()
                .setPaymentId(UUID.randomUUID())
                .setSagaId(UUID.randomUUID())
                .setPaymentId(paymentCancelledEvent.getPayment().getId().getValue())
                .setCustomerId(paymentCancelledEvent.getPayment().getCustomerId().getValue())
                .setOrderId(paymentCancelledEvent.getPayment().getOrderId().getValue())
                .setPrice(paymentCancelledEvent.getPayment().getPrice().getAmount())
                .setCreatedAt(paymentCancelledEvent.getPayment().getCreatedAt().toInstant())
                .setPaymentStatus(PaymentStatus.valueOf(paymentCancelledEvent.getPayment().getPaymentStatus().toString()))
                .setFailureMessages(paymentCancelledEvent.getFailureMessages())
                .build();
    }

    public PaymentResponseAvroModel paymentFailedEventToPaymentResponseAvroModel(PaymentFailedEvent paymentFailedEvent) {
        return PaymentResponseAvroModel.newBuilder()
                .setPaymentId(UUID.randomUUID())
                .setSagaId(UUID.randomUUID())
                .setPaymentId(paymentFailedEvent.getPayment().getId().getValue())
                .setCustomerId(paymentFailedEvent.getPayment().getCustomerId().getValue())
                .setOrderId(paymentFailedEvent.getPayment().getOrderId().getValue())
                .setPrice(paymentFailedEvent.getPayment().getPrice().getAmount())
                .setCreatedAt(paymentFailedEvent.getPayment().getCreatedAt().toInstant())
                .setPaymentStatus(PaymentStatus.valueOf(paymentFailedEvent.getPayment().getPaymentStatus().toString()))
                .setFailureMessages(paymentFailedEvent.getFailureMessages())
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
