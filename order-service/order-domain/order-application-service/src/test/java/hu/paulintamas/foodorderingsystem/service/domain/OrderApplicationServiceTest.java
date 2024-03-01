package hu.paulintamas.foodorderingsystem.service.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderStatus;
import hu.paulintamas.foodorderingsystem.domain.valueobject.PaymentOrderStatus;
import hu.paulintamas.foodorderingsystem.outbox.OutboxStatus;
import hu.paulintamas.foodorderingsystem.saga.SagaStatus;
import hu.paulintamas.foodorderingsystem.service.domain.dto.create.CreateOrderCommand;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Customer;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Order;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Restaurant;
import hu.paulintamas.foodorderingsystem.service.domain.exception.OrderDomainException;
import hu.paulintamas.foodorderingsystem.service.domain.mapper.OrderDataMapper;
import hu.paulintamas.foodorderingsystem.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import hu.paulintamas.foodorderingsystem.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import hu.paulintamas.foodorderingsystem.service.domain.ports.input.OrderApplicationService;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.repository.CustomerRepository;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.repository.OrderRepository;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.repository.PaymentOutboxRepository;
import hu.paulintamas.foodorderingsystem.service.domain.ports.output.repository.RestaurantRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static hu.paulintamas.foodorderingsystem.saga.order.SagaConstants.ORDER_SAGA_NAME;
import static hu.paulintamas.foodorderingsystem.service.domain.TestDataHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@TestInstance(
        value = TestInstance.Lifecycle.PER_METHOD
)
@SpringBootTest(
        classes = OrderTestConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
public class OrderApplicationServiceTest {

    @Autowired
    private OrderApplicationService orderApplicationService;
    @Autowired
    private OrderDataMapper orderDataMapper;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private PaymentOutboxRepository paymentOutboxRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private Order order;
    private Customer customer;
    private CreateOrderCommand createOrderCommand;
    private Restaurant restaurant;

    @BeforeEach
    void setup() {
        customer = createCustomer();
        restaurant = createRestaurant();

        given(customerRepository.findCustomer(CUSTOMER_ID_ONE)).
                willReturn(Optional.of(customer));
        given(restaurantRepository.findRestaurantInformation(any(Restaurant.class)))
                .willReturn(Optional.of(restaurant));
        given(paymentOutboxRepository.save(any(OrderPaymentOutboxMessage.class)))
                .willReturn(getOrderPaymentOutboxMessage());
    }

    @Test
    void testCreateOrder() {
        // given
        createOrderCommand = createCreateOrderCommand();
        setupOrder(createOrderCommand);

        // when
        var response = orderApplicationService.createOrder(createOrderCommand);

        // then
        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.getMessage()).isEqualTo("Order Created Successfully");
        assertThat(response.getOrderTrackingId()).isNotNull();
    }

    @Test
    void testCreateOrderWithWrongPrice() {
        // given
        createOrderCommand = createCreateOrderCommandWithWrongPrice();
        setupOrder(createOrderCommand);

        // when - then
        assertThatThrownBy(() -> orderApplicationService.createOrder(createOrderCommand))
                .isExactlyInstanceOf(OrderDomainException.class)
                .hasMessage("Total price: 250.00 is not equal to Order items total price: 200.00!");
    }

    @Test
    void testCreateOrderWithWrongProductPrice() {
        // given
        createOrderCommand = createCreateOrderCommandWithWrongProductPrice();
        setupOrder(createOrderCommand);

        // when - then
        assertThatThrownBy(() -> orderApplicationService.createOrder(createOrderCommand))
                .isExactlyInstanceOf(OrderDomainException.class)
                .hasMessage("order item price: 60.00 is not valid for product: 2de9c2b9-d822-4516-94ec-c21c62bde0e6");
    }

    @Test
    void testCreateOrderWithNotActiveRestaurant() {
        // given
        restaurant.setActive(false);
        createOrderCommand = createCreateOrderCommand();
        setupOrder(createOrderCommand);

        // when - then
        assertThatThrownBy(() -> orderApplicationService.createOrder(createOrderCommand))
                .isExactlyInstanceOf(OrderDomainException.class)
                .hasMessage("Restaurant with id: 5b479216-e452-4d77-b6fd-46cab2702403 is inactive!");
    }

    private void setupOrder(CreateOrderCommand createOrderCommand) {
        order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        order.setId(ORDER_ID_ENTITY_ONE);
        given(orderRepository.save(any(Order.class)))
                .willReturn(order);
    }

    private OrderPaymentOutboxMessage getOrderPaymentOutboxMessage() {
        return OrderPaymentOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(SAGA_ID)
                .createdAt(ZonedDateTime.now())
                .type(ORDER_SAGA_NAME)
                .payload(createPayload(getOrderPaymentEventPayload()))
                .orderStatus(OrderStatus.PENDING)
                .sagaStatus(SagaStatus.STARTED)
                .outboxStatus(OutboxStatus.STARTED)
                .version(0)
                .build();
    }

    @SneakyThrows
    private String createPayload(OrderPaymentEventPayload orderPaymentEventPayload) {
        return objectMapper.writeValueAsString(orderPaymentEventPayload);
    }

    private OrderPaymentEventPayload getOrderPaymentEventPayload() {
        return OrderPaymentEventPayload.builder()
                .orderId(ORDER_ID_ONE.toString())
                .customerId(CUSTOMER_ID_ONE.toString())
                .price(PRICE_50.getAmount())
                .createdAt(ZonedDateTime.now())
                .paymentOrderStatus(PaymentOrderStatus.PENDING.name())
                .build();
    }

}
