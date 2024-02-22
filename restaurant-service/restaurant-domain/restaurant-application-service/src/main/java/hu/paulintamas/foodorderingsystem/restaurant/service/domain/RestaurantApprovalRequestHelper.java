package hu.paulintamas.foodorderingsystem.restaurant.service.domain;

import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderId;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.dto.RestaurantApprovalRequest;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity.Restaurant;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.event.OrderApprovalEvent;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.exception.RestaurantNotFoundException;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.mapper.RestaurantDataMapper;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.ports.output.message.publisher.OrderApprovedMessagePublisher;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.ports.output.message.publisher.OrderRejectedMessagePublisher;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.ports.output.repository.OrderApprovalRepository;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.ports.output.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantApprovalRequestHelper {

    private final RestaurantDomainService restaurantDomainService;
    private final RestaurantDataMapper restaurantDataMapper;
    private final RestaurantRepository restaurantRepository;
    private final OrderApprovalRepository orderApprovalRepository;
    private final OrderApprovedMessagePublisher orderApprovedMessagePublisher;
    private final OrderRejectedMessagePublisher orderRejectedMessagePublisher;

    @Transactional
    public OrderApprovalEvent persistOrderApproval(RestaurantApprovalRequest restaurantApprovalRequest) {
        log.info(
                "Processing restaurant approval for order: {}",
                restaurantApprovalRequest.getOrderId()
        );
        var failureMessages = new ArrayList<String>();
        var restaurant = findRestaurant(restaurantApprovalRequest);
        var orderApprovalEvent = restaurantDomainService.validateOrder(restaurant, failureMessages, orderApprovedMessagePublisher, orderRejectedMessagePublisher);
        orderApprovalRepository.save(restaurant.getOrderApproval());
        return orderApprovalEvent;
    }

    private Restaurant findRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {
        var restaurant = restaurantDataMapper.restaurantApprovalRequestToRestaurant(restaurantApprovalRequest);
        var result = restaurantRepository.findRestaurantInformation(restaurant);
        if (result.isEmpty()) {
            log.error(
                    "Restaurant with id: {} not found!",
                    restaurant.getId().getValue()
            );
            throw new RestaurantNotFoundException("Restaurant with id: " + restaurant.getId().getValue() + " not found!");
        }
        var entity = result.get();
        restaurant.setActive(entity.isActive());
        restaurant.getOrderDetail().getProducts().forEach(product -> entity.getOrderDetail().getProducts().forEach(entityProduct -> {
            if (entityProduct.getId().equals(product.getId())) {
                product.updateWithConfirmedNamePriceAndAvailableFields(entityProduct.getName(), entityProduct.getPrice(), entityProduct.isAvailable());
            }
        }));

        restaurant.getOrderDetail().setId(OrderId.builder().value(UUID.fromString(restaurantApprovalRequest.getOrderId())).build());

        return restaurant;
    }
}
