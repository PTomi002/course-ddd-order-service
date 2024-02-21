package hu.paulintamas.foodorderingsystem.payment.service.domain;

import hu.paulintamas.foodorderingsystem.domain.event.publisher.DomainEventPublisher;
import hu.paulintamas.foodorderingsystem.domain.valueobject.Money;
import hu.paulintamas.foodorderingsystem.domain.valueobject.PaymentStatus;
import hu.paulintamas.foodorderingsystem.payment.service.domain.entity.CreditEntry;
import hu.paulintamas.foodorderingsystem.payment.service.domain.entity.CreditHistory;
import hu.paulintamas.foodorderingsystem.payment.service.domain.entity.Payment;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentCancelledEvent;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentCompletedEvent;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentEvent;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentFailedEvent;
import hu.paulintamas.foodorderingsystem.payment.service.domain.valueobject.CreditHistoryId;
import hu.paulintamas.foodorderingsystem.payment.service.domain.valueobject.TransactionType;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
public class PaymentDomainServiceImpl implements PaymentDomainService {

    @Override
    public PaymentEvent validateAndInitiatePayment(
            Payment payment,
            CreditEntry creditEntry,
            List<CreditHistory> creditHistories,
            List<String> failureMessages,
            DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher,
            DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher
    ) {
        payment.validatePayment(failureMessages);
        payment.initializePayment();
        validateCreditEntry(payment, creditEntry, failureMessages);
        subtractCreditEntry(payment, creditEntry);
        updateCreditHistory(payment, creditHistories, TransactionType.DEBIT);
        validateCreditHistory(creditEntry, creditHistories, failureMessages);

        if (failureMessages.isEmpty()) {
            log.info("Payment is initiated for order: {}", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.COMPLETED);
            return PaymentCompletedEvent.builder()
                    .payment(payment)
                    .zonedDateTime(ZonedDateTime.now())
                    .failureMessages(List.of())
                    .paymentCompletedEventDomainEventPublisher(paymentCompletedEventDomainEventPublisher)
                    .build();
        } else {
            log.info("Payment initiation is failed for order: {}", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.FAILED);
            return PaymentFailedEvent.builder()
                    .payment(payment)
                    .zonedDateTime(ZonedDateTime.now())
                    .failureMessages(failureMessages)
                    .paymentFailedEventDomainEventPublisher(paymentFailedEventDomainEventPublisher)
                    .build();
        }
    }

    @Override
    public PaymentEvent validateAndCancelEvent(
            Payment payment,
            CreditEntry creditEntry,
            List<CreditHistory> creditHistories,
            List<String> failureMessages,
            DomainEventPublisher<PaymentCancelledEvent> paymentCancelledEventDomainEventPublisher,
            DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher
    ) {
        payment.validatePayment(failureMessages);
        addCreditEntry(payment, creditEntry);
        updateCreditHistory(payment, creditHistories, TransactionType.CREDIT);

        if (failureMessages.isEmpty()) {
            log.info("Payment is cancelled for order: {}", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.CANCELLED);
            return PaymentCancelledEvent.builder()
                    .payment(payment)
                    .zonedDateTime(ZonedDateTime.now())
                    .failureMessages(List.of())
                    .paymentCancelledEventDomainEventPublisher(paymentCancelledEventDomainEventPublisher)
                    .build();
        } else {
            log.info("Payment cancel is failed for order: {}", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.FAILED);
            return PaymentFailedEvent.builder()
                    .payment(payment)
                    .zonedDateTime(ZonedDateTime.now())
                    .failureMessages(failureMessages)
                    .paymentFailedEventDomainEventPublisher(paymentFailedEventDomainEventPublisher)
                    .build();
        }
    }

    private void addCreditEntry(Payment payment, CreditEntry creditEntry) {
        creditEntry.addCreditAmount(payment.getPrice());
    }

    private void validateCreditHistory(CreditEntry creditEntry, List<CreditHistory> creditHistories, List<String> failureMessages) {
        var totalCreditHistory = getTotalHistoryOf(creditHistories, TransactionType.CREDIT);
        var totalDebitHistory = getTotalHistoryOf(creditHistories, TransactionType.DEBIT);

        if (totalDebitHistory.isGreaterThan(totalCreditHistory)) {
            log.error(
                    "Customer with id: {} does not have enough credit according to the redit history!",
                    creditEntry.getCustomerId().getValue()
            );
            failureMessages.add("Customer with id: " + creditEntry.getCustomerId().getValue() + " does not have enough credit according to the redit history!");
        }

        if (!creditEntry.getTotalCreditAmount().equals(totalCreditHistory.subtract(totalDebitHistory))) {
            log.error(
                    "Customer history total is not equal to the current credit for customer: {}",
                    creditEntry.getCustomerId().getValue()
            );
            failureMessages.add("Customer history total is not equal to the current credit for customer: " + creditEntry.getCustomerId().getValue());
        }

    }

    private Money getTotalHistoryOf(List<CreditHistory> creditHistories, TransactionType transactionType) {
        return creditHistories.stream()
                .filter(creditHistory -> transactionType == creditHistory.getTransactionType())
                .map(CreditHistory::getAmount)
                .reduce(Money.ZERO, Money::add);
    }

    private void updateCreditHistory(Payment payment, List<CreditHistory> creditHistories, TransactionType transactionType) {
        creditHistories.add(
                CreditHistory.builder()
                        .id(CreditHistoryId.builder().id(UUID.randomUUID()).build())
                        .customerId(payment.getCustomerId())
                        .amount(payment.getPrice())
                        .transactionType(transactionType)
                        .build()
        );
    }

    private void subtractCreditEntry(Payment payment, CreditEntry creditEntry) {
        creditEntry.subtractCreditAmount(payment.getPrice());
    }

    private void validateCreditEntry(Payment payment, CreditEntry creditEntry, List<String> failureMessages) {
        if (payment.getPrice().isGreaterThan(creditEntry.getTotalCreditAmount())) {
            log.error(
                    "Customer with id: {} does not have enough credit for payment!",
                    payment.getCustomerId().getValue()
            );
            failureMessages.add("Customer with id: " + payment.getCustomerId().getValue() + " does not have enough credit for payment!");
        }
    }
}
