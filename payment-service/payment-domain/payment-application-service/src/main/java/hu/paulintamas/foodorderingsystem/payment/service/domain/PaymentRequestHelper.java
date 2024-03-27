package hu.paulintamas.foodorderingsystem.payment.service.domain;

import hu.paulintamas.foodorderingsystem.domain.event.publisher.DomainEventPublisher;
import hu.paulintamas.foodorderingsystem.domain.valueobject.CustomerId;
import hu.paulintamas.foodorderingsystem.domain.valueobject.PaymentStatus;
import hu.paulintamas.foodorderingsystem.outbox.OutboxStatus;
import hu.paulintamas.foodorderingsystem.payment.service.domain.dto.PaymentRequest;
import hu.paulintamas.foodorderingsystem.payment.service.domain.entity.CreditEntry;
import hu.paulintamas.foodorderingsystem.payment.service.domain.entity.CreditHistory;
import hu.paulintamas.foodorderingsystem.payment.service.domain.entity.Payment;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentCancelledEvent;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentCompletedEvent;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentEvent;
import hu.paulintamas.foodorderingsystem.payment.service.domain.event.PaymentFailedEvent;
import hu.paulintamas.foodorderingsystem.payment.service.domain.exception.PaymentApplicationServiceException;
import hu.paulintamas.foodorderingsystem.payment.service.domain.exception.PaymentNotFoundException;
import hu.paulintamas.foodorderingsystem.payment.service.domain.mapper.PaymentDataMapper;
import hu.paulintamas.foodorderingsystem.payment.service.domain.outbox.scheduler.OrderOutboxHelper;
import hu.paulintamas.foodorderingsystem.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
import hu.paulintamas.foodorderingsystem.payment.service.domain.ports.output.repository.CreditEntryRepository;
import hu.paulintamas.foodorderingsystem.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import hu.paulintamas.foodorderingsystem.payment.service.domain.ports.output.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentRequestHelper {

    private final PaymentDomainService paymentDomainService;
    private final PaymentDataMapper paymentDataMapper;
    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final OrderOutboxHelper orderOutboxHelper;
    private final PaymentResponseMessagePublisher paymentResponseMessagePublisher;

    @Transactional
    public void persistPayment(PaymentRequest paymentRequest) {
        // If payment-response is somehow not consumed successfully by OrderService
        //      it might ask for a payment again for the same saga id.
        // If we see an already completed payment message we should not process the payment again, instead
        //      assume that the payment message is not processed by the OrderService.
        if (publishIfOutboxMessageProcessedForPayment(paymentRequest, PaymentStatus.COMPLETED)) {
            log.info(
                    "An outbox message has already saved to the database with saga id: {}",
                    paymentRequest.getSagaId()
            );
            return;
        }

        log.info(
                "Received payment complete event for order id: {}",
                paymentRequest.getOrderId()
        );
        var payment = paymentDataMapper.paymentRequestModelToPayment(paymentRequest);
        var creditEntry = getCreditEntry(payment.getCustomerId());
        var creditHistories = getCreditHistory(payment.getCustomerId());
        var failureMessages = new ArrayList<String>();
        var paymentEvent = paymentDomainService.validateAndInitiatePayment(
                payment, creditEntry, creditHistories, failureMessages
        );
        persistDbObjects(payment, creditEntry, creditHistories, failureMessages);

        orderOutboxHelper.saveOrderOutboxMessage(
                paymentDataMapper.paymentEventToOrderEventPayload(paymentEvent),
                paymentEvent.getPayment().getPaymentStatus(),
                OutboxStatus.STARTED,
                UUID.fromString(paymentRequest.getSagaId())
        );
    }

    @Transactional
    public void persistCancelPayment(PaymentRequest paymentRequest) {
        if (publishIfOutboxMessageProcessedForPayment(paymentRequest, PaymentStatus.CANCELLED)) {
            log.info(
                    "An outbox message has already saved to the database with saga id: {}",
                    paymentRequest.getSagaId()
            );
            return;
        }

        log.info(
                "Received payment rollback event for order id: {}",
                paymentRequest.getOrderId()
        );
        var paymentResponse = paymentRepository.findByOrderId(UUID.fromString(paymentRequest.getOrderId()));
        if (paymentResponse.isEmpty()) {
            log.error(
                    "Could not find payment with order id: {}",
                    paymentRequest.getOrderId()
            );
            throw new PaymentNotFoundException("Could not find payment with order id: " + paymentRequest.getOrderId());
        }
        var payment = paymentResponse.get();
        var creditEntry = getCreditEntry(payment.getCustomerId());
        var creditHistories = getCreditHistory(payment.getCustomerId());
        var failureMessages = new ArrayList<String>();
        var paymentEvent = paymentDomainService.validateAndCancelEvent(
                payment, creditEntry, creditHistories, failureMessages
        );
        persistDbObjects(payment, creditEntry, creditHistories, failureMessages);

        orderOutboxHelper.saveOrderOutboxMessage(
                paymentDataMapper.paymentEventToOrderEventPayload(paymentEvent),
                paymentEvent.getPayment().getPaymentStatus(),
                OutboxStatus.STARTED,
                UUID.fromString(paymentRequest.getSagaId())
        );
    }

    private void persistDbObjects(Payment payment, CreditEntry creditEntry, List<CreditHistory> creditHistories, ArrayList<String> failureMessages) {
        paymentRepository.save(payment);
        if (failureMessages.isEmpty()) {
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistories.get(creditHistories.size() - 1));
        }
    }

    private List<CreditHistory> getCreditHistory(CustomerId customerId) {
        var creditHistory = creditHistoryRepository.findByCustomerId(customerId);
        if (creditHistory.isEmpty()) {
            log.error(
                    "Could not find credit history for customer: {}",
                    customerId.getValue()
            );
            throw new PaymentApplicationServiceException("Could not find credit history for customer: " + customerId.getValue());
        }
        return creditHistory.get();
    }

    private CreditEntry getCreditEntry(CustomerId customerId) {
        var creditEntry = creditEntryRepository.findByCustomerId(customerId);
        if (creditEntry.isEmpty()) {
            log.error(
                    "Could not find credit entry for customer: {}",
                    customerId.getValue()
            );
            throw new PaymentApplicationServiceException("Could not find credit entry for customer: " + customerId.getValue());
        }
        return creditEntry.get();
    }

    private boolean publishIfOutboxMessageProcessedForPayment(
            PaymentRequest paymentRequest,
            PaymentStatus paymentStatus
    ) {
        var orderOutboxMessage = orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(
                UUID.fromString(paymentRequest.getSagaId()),
                paymentStatus
        );
        if (orderOutboxMessage.isPresent()) {
            paymentResponseMessagePublisher.publish(orderOutboxMessage.get(), orderOutboxHelper::updateOutboxMessage);
            return true;
        } else {
            return false;
        }
    }

}
