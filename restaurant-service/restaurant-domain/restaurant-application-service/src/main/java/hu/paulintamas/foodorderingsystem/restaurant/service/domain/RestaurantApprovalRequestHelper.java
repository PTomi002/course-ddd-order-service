package hu.paulintamas.foodorderingsystem.restaurant.service.domain;

import hu.paulintamas.foodorderingsystem.domain.valueobject.OrderId;
import hu.paulintamas.foodorderingsystem.outbox.OutboxStatus;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.dto.RestaurantApprovalRequest;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.entity.Restaurant;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.exception.RestaurantNotFoundException;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.mapper.RestaurantDataMapper;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.outbox.scheduler.RestaurantOrderOutboxHelper;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.ports.input.messagel.listener.RestaurantApprovalRequestMessageListener;
import hu.paulintamas.foodorderingsystem.restaurant.service.domain.ports.output.message.publisher.RestaurantApprovalResponseMessagePublisher;
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
    private final RestaurantOrderOutboxHelper restaurantOrderOutboxHelper;
    private final RestaurantApprovalResponseMessagePublisher restaurantApprovalResponseMessagePublisher;

    @Transactional
    public void persistOrderApproval(RestaurantApprovalRequest restaurantApprovalRequest) {
        if (publishIfOutboxMessageProcessed(restaurantApprovalRequest)) {
            log.info(
                    "An outbox message with saga id: {} already saved to the database",
                    restaurantApprovalRequest.getSagaId()
            );
            return;
        }

        log.info(
                "Processing restaurant approval for order: {}",
                restaurantApprovalRequest.getOrderId()
        );
        var failureMessages = new ArrayList<String>();
        var restaurant = findRestaurant(restaurantApprovalRequest);
        var orderApprovalEvent = restaurantDomainService.validateOrder(restaurant, failureMessages);
        orderApprovalRepository.save(restaurant.getOrderApproval());

        restaurantOrderOutboxHelper
                .saveOrderOutboxMessage(
                    restaurantDataMapper.orderApprovalEventToOrderEventPayload(orderApprovalEvent),
                        orderApprovalEvent.getOrderApproval().getOrderApprovalStatus(),
                        OutboxStatus.STARTED,
                        UUID.fromString(restaurantApprovalRequest.getSagaId())
                );
    }

    // check if the message is already processed, in that case I publish the message again
    private boolean publishIfOutboxMessageProcessed(RestaurantApprovalRequest restaurantApprovalRequest) {
        var orderOutboxMessage = restaurantOrderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndOutboxStatus(
                UUID.fromString(restaurantApprovalRequest.getSagaId()),
                OutboxStatus.COMPLETED
        );
        if (orderOutboxMessage.isPresent()) {
            restaurantApprovalResponseMessagePublisher.publish(
                    orderOutboxMessage.get(),
                    restaurantOrderOutboxHelper::updateOutboxStatus
            );
            return true;
        }
        return false;
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
