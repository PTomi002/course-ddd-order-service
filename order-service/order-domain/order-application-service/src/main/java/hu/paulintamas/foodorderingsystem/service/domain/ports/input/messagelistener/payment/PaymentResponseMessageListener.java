package hu.paulintamas.foodorderingsystem.service.domain.ports.input.messagelistener.payment;

import hu.paulintamas.foodorderingsystem.service.domain.dto.message.PaymentResponse;

/**
 * [KNOWLEDGE]
 * <p>
 * Domain Event Listener: special input ports, triggered by special domain events, not by clients
 * <p>
 * Can be called via two ways:
 * - as part of Saga pattern impl.
 * - as part of business invariant failed
 */
public interface PaymentResponseMessageListener {

    void paymentCompleted(PaymentResponse paymentResponse);

    void paymentCancelled(PaymentResponse paymentResponse);
}
