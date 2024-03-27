package hu.paulintamas.foodorderingsystem.payment.service.domain;

import hu.paulintamas.foodorderingsystem.payment.service.domain.entity.CreditEntry;
import hu.paulintamas.foodorderingsystem.payment.service.domain.entity.CreditHistory;
import hu.paulintamas.foodorderingsystem.payment.service.domain.entity.Payment;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentEvent;

import java.util.List;

public interface PaymentDomainService {

    PaymentEvent validateAndInitiatePayment(
            Payment payment,
            CreditEntry creditEntry,
            List<CreditHistory> creditHistories,
            List<String> failureMessages
    );

    PaymentEvent validateAndCancelEvent(
            Payment payment,
            CreditEntry creditEntry,
            List<CreditHistory> creditHistories,
            List<String> failureMessages
    );

}
