package hu.paulintamas.foodorderingsystem.orderservice;

import hu.paulintamas.foodorderingsystem.domain.valueobject.PaymentStatus;
import hu.paulintamas.foodorderingsystem.order.service.dataaccess.outbox.payment.repository.PaymentOutboxJpaRepository;
import hu.paulintamas.foodorderingsystem.saga.SagaStatus;
import hu.paulintamas.foodorderingsystem.service.domain.OrderPaymentSaga;
import hu.paulintamas.foodorderingsystem.service.domain.dto.message.PaymentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static hu.paulintamas.foodorderingsystem.saga.order.SagaConstants.ORDER_SAGA_NAME;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = OrderServiceApplication.class)
@Sql(value = {"classpath:sql/OrderPaymentSagaTestSetup.sql"})
@Sql(value = {"classpath:sql/OrderPaymentSagaTestCleanup.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class OrderPaymentSagaTest {

    @Autowired
    private OrderPaymentSaga orderPaymentSaga;
    @Autowired
    private PaymentOutboxJpaRepository paymentOutboxJpaRepository;

    private final UUID SAGA_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afa");
    private final UUID ORDER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb17");
    private final UUID CUSTOMER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");
    private final UUID PAYMENT_ID = UUID.randomUUID();
    private final BigDecimal PRICE = new BigDecimal("100");

    @Test
    @DisplayName("Test idempotent consuming from payment service")
    void testDoublePayment() {
        // when
        orderPaymentSaga.process(createPaymentResponse());
        orderPaymentSaga.process(createPaymentResponse());
    }

    @Test
    @DisplayName("Test idempotent consuming from payment service")
    void testDoublePaymentWithThreads() throws InterruptedException {
        // when
        var threadOne = new Thread(() -> orderPaymentSaga.process(createPaymentResponse()));
        var threadTwo = new Thread(() -> orderPaymentSaga.process(createPaymentResponse()));

        threadOne.start();
        threadTwo.start();

        threadOne.join();
        threadTwo.join();

        assertPaymentOutbox();
    }

    private void assertPaymentOutbox() {
        var paymentOutboxEntity = paymentOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(
                ORDER_SAGA_NAME,
                SAGA_ID,
                List.of(SagaStatus.SUCCEEDED)
        );
        assertThat(paymentOutboxEntity.isPresent()).isTrue();
    }

    // comes from the PaymentService
    private PaymentResponse createPaymentResponse() {
        return PaymentResponse.builder()
                .id(UUID.randomUUID().toString())
                .sagaId(SAGA_ID.toString())
                .paymentStatus(PaymentStatus.COMPLETED)
                .orderId(ORDER_ID.toString())
                .customerId(CUSTOMER_ID.toString())
                .price(PRICE)
                .createdAt(Instant.now())
                .failureMessages(new ArrayList<>())
                .build();
    }

}
