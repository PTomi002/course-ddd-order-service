package hu.paulintamas.foodorderingsystem.service.domain;

import hu.paulintamas.foodorderingsystem.domain.valueobject.*;
import hu.paulintamas.foodorderingsystem.service.domain.dto.create.CreateOrderCommand;
import hu.paulintamas.foodorderingsystem.service.domain.dto.create.OrderAddress;
import hu.paulintamas.foodorderingsystem.service.domain.dto.create.OrderItem;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Customer;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Product;
import hu.paulintamas.foodorderingsystem.service.domain.entity.Restaurant;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class TestDataHelper {

    static final String POSTAL_CODE_ONE = "postalcode_1";
    static final String CITY_ONE = "city_1";
    static final String STREET_ONE = "street_1";
    static final String PRODUCT_NAME_ONE = "product_name_1";
    static final String PRODUCT_NAME_TWO = "product_name_2";

    static final Integer QUANTITY_1 = 1;
    static final Integer QUANTITY_3 = 3;

    final static UUID CUSTOMER_ID_ONE = UUID.fromString("abaf8a38-c345-4c39-9861-310bf4c55af8");
    final static UUID RESTAURANT_ID_ONE = UUID.fromString("5b479216-e452-4d77-b6fd-46cab2702403");
    final static UUID PRODUCT_ID_ONE = UUID.fromString("2de9c2b9-d822-4516-94ec-c21c62bde0e6");
    final static UUID PRODUCT_ID_TWO = UUID.fromString("2c9b5e0c-d39d-4016-98f2-632581b8957f");
    final static UUID ORDER_ID_ONE = UUID.fromString("09848003-e5b8-4c0a-b988-dcab7aefaeba");

    final static BigDecimal ORDER_PRICE_200 = new BigDecimal("200.00");
    final static BigDecimal ORDER_PRICE_250 = new BigDecimal("250.00");
    final static BigDecimal ORDER_PRICE_210 = new BigDecimal("210.00");
    final static BigDecimal ITEM_PRICE_50 = new BigDecimal("50.00");
    final static BigDecimal ITEM_PRICE_60 = new BigDecimal("60.00");
    final static BigDecimal SUBTOTAL_150 = new BigDecimal("150.00");
    final static BigDecimal SUBTOTAL_50 = new BigDecimal("50.00");
    final static BigDecimal SUBTOTAL_60 = new BigDecimal("60.00");

    static final Money PRICE_50 = Money.of(ITEM_PRICE_50);
    static final Money PRICE_60 = Money.of(ITEM_PRICE_60);

    static final CustomerId CUSTOMER_ID_ENTITY_ONE = CustomerId.builder().value(CUSTOMER_ID_ONE).build();
    static final RestaurantId RESTAURANT_ID_ENTITY_ONE = RestaurantId.builder().value(RESTAURANT_ID_ONE).build();
    static final ProductId PRODUCT_ID_ENTITY_ONE = ProductId.builder().value(PRODUCT_ID_ONE).build();
    static final ProductId PRODUCT_ID_ENTITY_TWO = ProductId.builder().value(PRODUCT_ID_TWO).build();
    static final OrderId ORDER_ID_ENTITY_ONE = OrderId.builder().value(ORDER_ID_ONE).build();

    CreateOrderCommand createCreateOrderCommand() {
        return CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID_ONE)
                .restaurantId(RESTAURANT_ID_ONE)
                .address(createOrderAddress(POSTAL_CODE_ONE, CITY_ONE, STREET_ONE))
                .price(ORDER_PRICE_200)
                .orderItems(List.of(
                        createOrderItem(PRODUCT_ID_ONE, ITEM_PRICE_50, SUBTOTAL_150, QUANTITY_3),
                        createOrderItem(PRODUCT_ID_ONE, ITEM_PRICE_50, SUBTOTAL_50, QUANTITY_1)
                ))
                .build();
    }

    CreateOrderCommand createCreateOrderCommandWithWrongPrice() {
        return CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID_ONE)
                .restaurantId(RESTAURANT_ID_ONE)
                .address(createOrderAddress(POSTAL_CODE_ONE, CITY_ONE, STREET_ONE))
                .price(ORDER_PRICE_250)
                .orderItems(List.of(
                        createOrderItem(PRODUCT_ID_ONE, ITEM_PRICE_50, SUBTOTAL_150, QUANTITY_3),
                        createOrderItem(PRODUCT_ID_ONE, ITEM_PRICE_50, SUBTOTAL_50, QUANTITY_1)
                ))
                .build();
    }

    CreateOrderCommand createCreateOrderCommandWithWrongProductPrice() {
        return CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID_ONE)
                .restaurantId(RESTAURANT_ID_ONE)
                .address(createOrderAddress(POSTAL_CODE_ONE, CITY_ONE, STREET_ONE))
                .price(ORDER_PRICE_210)
                .orderItems(List.of(
                        createOrderItem(PRODUCT_ID_ONE, ITEM_PRICE_50, SUBTOTAL_150, QUANTITY_3),
                        createOrderItem(PRODUCT_ID_ONE, ITEM_PRICE_60, SUBTOTAL_60, QUANTITY_1)
                ))
                .build();
    }

    Customer createCustomer() {
        return Customer.builder()
                .id(CUSTOMER_ID_ENTITY_ONE)
                .build();
    }

    Restaurant createRestaurant() {
        return Restaurant.builder()
                .id(RESTAURANT_ID_ENTITY_ONE)
                .products(List.of(
                        createProduct(PRODUCT_ID_ENTITY_ONE, PRODUCT_NAME_ONE, PRICE_50),
                        createProduct(PRODUCT_ID_ENTITY_TWO, PRODUCT_NAME_TWO, PRICE_60)
                ))
                .active(true)
                .build();
    }

    private Product createProduct(ProductId productId,
                                  String name,
                                  Money price) {
        return Product.builder()
                .price(price)
                .name(name)
                .id(productId)
                .build();
    }

    private OrderItem createOrderItem(UUID productId,
                                      BigDecimal price,
                                      BigDecimal subTotal,
                                      int quantity) {
        return OrderItem.builder()
                .productId(productId)
                .quantity(quantity)
                .price(price)
                .subTotal(subTotal)
                .build();
    }

    private OrderAddress createOrderAddress(String postalCode,
                                            String city,
                                            String street) {
        return OrderAddress.builder()
                .street(street)
                .city(city)
                .postalCode(postalCode)
                .build();
    }

}
