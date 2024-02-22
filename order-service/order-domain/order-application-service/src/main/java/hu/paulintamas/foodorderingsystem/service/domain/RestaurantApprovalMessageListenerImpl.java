package hu.paulintamas.foodorderingsystem.service.domain;

import hu.paulintamas.foodorderingsystem.service.domain.dto.message.RestaurantApprovalResponse;
import hu.paulintamas.foodorderingsystem.service.domain.ports.input.messagelistener.restaurantapproval.RestaurantApprovalMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class RestaurantApprovalMessageListenerImpl implements RestaurantApprovalMessageListener {
    private final OrderApprovalSaga orderApprovalSaga;

    @Override
    public void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse) {
        orderApprovalSaga.process(restaurantApprovalResponse);
        log.info("Order is approved for order id: {}", restaurantApprovalResponse.getOrderId());
    }

    @Override
    public void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse) {
        var orderCancelled = orderApprovalSaga.rollback(restaurantApprovalResponse);
        log.info(
                "Publishing order cancelled event with failure messages: {} and with order id: {}",
                restaurantApprovalResponse.getFailureMessages(),
                restaurantApprovalResponse.getOrderId()
        );
        orderCancelled.fire();
    }
}
