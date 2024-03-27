package hu.paulintamas.foodorderingsystem.payment.service.domain;

import hu.paulintamas.foodorderingsystem.domain.valueobject.PaymentOrderStatus;
import hu.paulintamas.foodorderingsystem.domain.valueobject.PaymentStatus;
import hu.paulintamas.foodorderingsystem.outbox.OutboxStatus;
import hu.paulintamas.foodorderingsystem.payment.service.PaymentServiceApplication;
import hu.paulintamas.foodorderingsystem.payment.service.dataaccess.outbox.repository.OrderOutboxJpaRepository;
import hu.paulintamas.foodorderingsystem.payment.service.domain.dto.PaymentRequest;
import hu.paulintamas.foodorderingsystem.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import static hu.paulintamas.foodorderingsystem.saga.order.SagaConstants.ORDER_SAGA_NAME;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(classes = PaymentServiceApplication.class)
public class PaymentRequestMessageListenerTest {
    private static final String CUSTOMER_ID = "d215b5f8-0249-4dc5-89a3-51fd148cfb41";
    private static final BigDecimal PRICE = new BigDecimal("100");
    @Autowired
    private PaymentRequestMessageListener paymentRequestMessageListener;
    @Autowired
    private OrderOutboxJpaRepository orderOutboxJpaRepository;

    @Test
    @DisplayName("System does not allow to create multiple outbox messages for the same saga id")
    void testDoublePayment() {
        // given
        var sagaId = UUID.randomUUID().toString();

        // when
        paymentRequestMessageListener.completePayment(createPaymentRequest(sagaId));

        // then
        try {
            paymentRequestMessageListener.completePayment(createPaymentRequest(sagaId));
        } catch (DataAccessException e) {
            log.error(
                    "DataAccessException occurred with sql state: {}",
                    e.getMessage()
            );
        }
        assertOrderOutbox(sagaId);
    }

    @Test
    @DisplayName("System does not allow to create multiple outbox messages for the same saga id from multiple threads")
    void testDoublePaymentWithThreads() throws InterruptedException {
        // given
        var sagaId = UUID.randomUUID().toString();
        var executor = Executors.newFixedThreadPool(2);
        var tasks = new ArrayList<Callable<Object>>();

        tasks.add(Executors.callable(() -> {
            try {
                paymentRequestMessageListener.completePayment(createPaymentRequest(sagaId));
            } catch (DataAccessException e) {
                log.error(
                        "DataAccessException occurred with sql state in thread 1: {}",
                        e.getMessage()
                );
            }
        }));
        tasks.add(Executors.callable(() -> {
            try {
                paymentRequestMessageListener.completePayment(createPaymentRequest(sagaId));
            } catch (DataAccessException e) {
                log.error(
                        "DataAccessException occurred with sql state in thread 2: {}",
                        e.getMessage()
                );
            }
        }));

        // when
        executor.invokeAll(tasks);
        assertOrderOutbox(sagaId);

        // then
        executor.shutdown();
    }

    private void assertOrderOutbox(String sagaId) {
        var orderOutboxEntity = orderOutboxJpaRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
                ORDER_SAGA_NAME,
                UUID.fromString(sagaId),
                PaymentStatus.COMPLETED,
                OutboxStatus.STARTED
        );
        assertThat(orderOutboxEntity.isPresent()).isTrue();
        assertThat(orderOutboxEntity.get().getSagaId().toString()).isEqualTo(sagaId);
    }

    private PaymentRequest createPaymentRequest(String sagaId) {
        return PaymentRequest.builder()
                .id(UUID.randomUUID().toString())
                .sagaId(sagaId)
                .orderId(UUID.randomUUID().toString())
                .paymentOrderStatus(PaymentOrderStatus.PENDING)
                .customerId(CUSTOMER_ID)
                .price(PRICE)
                .createdAt(ZonedDateTime.now(Clock.systemUTC()).toInstant())
                .build();
    }
}
