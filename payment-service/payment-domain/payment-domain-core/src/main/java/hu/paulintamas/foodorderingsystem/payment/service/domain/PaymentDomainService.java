package hu.paulintamas.foodorderingsystem.payment.service.domain;

import hu.paulintamas.foodorderingsystem.domain.event.publisher.DomainEventPublisher;
import hu.paulintamas.foodorderingsystem.payment.service.domain.entity.CreditEntry;
import hu.paulintamas.foodorderingsystem.payment.service.domain.entity.CreditHistory;
import hu.paulintamas.foodorderingsystem.payment.service.domain.entity.Payment;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentCancelledEvent;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentCompletedEvent;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentEvent;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentFailedEvent;

import java.util.List;

public interface PaymentDomainService {

    PaymentEvent validateAndInitiatePayment(
            Payment payment,
            CreditEntry creditEntry,
            List<CreditHistory> creditHistories,
            List<String> failureMessages,
            DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher,
            DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher
    );

    PaymentEvent validateAndCancelEvent(
            Payment payment,
            CreditEntry creditEntry,
            List<CreditHistory> creditHistories,
            List<String> failureMessages,
            DomainEventPublisher<PaymentCancelledEvent> paymentCancelledEventDomainEventPublisher,
            DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher
    );

}
