package hu.paulintamas.foodorderingsystem.order.service.application.rest;

import hu.paulintamas.foodorderingsystem.service.domain.dto.create.CreateOrderCommand;
import hu.paulintamas.foodorderingsystem.service.domain.dto.create.CreateOrderResponse;
import hu.paulintamas.foodorderingsystem.service.domain.dto.track.TrackOrderQuery;
import hu.paulintamas.foodorderingsystem.service.domain.dto.track.TrackOrderResponse;
import hu.paulintamas.foodorderingsystem.service.domain.ports.input.OrderApplicationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * [KNOWLEDGE]
 * JSON API + API Versioning Option
 */
@Slf4j
@RestController
@RequestMapping(
        value = "/v1/orders",
        produces = "application/vnd.api.v1+json"
)
@AllArgsConstructor
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(
            @RequestBody CreateOrderCommand createOrderCommand
    ) {
        log.info("Message='Creating order for customer', customer={}, restaurant={}", createOrderCommand.getCustomerId(), createOrderCommand.getRestaurantId());
        var response = orderApplicationService.createOrder(createOrderCommand);
        log.info("Message='Order created with tracking id', trackingId={}", response.getOrderTrackingId());
        return ResponseEntity.ok(response);
    }

    @GetMapping(
            path = "/{trackingId}"
    )
    public ResponseEntity<TrackOrderResponse> trackOrder(
            @PathVariable UUID trackingId
    ) {
        log.info("Message='Get order for tracking id', trackingId={}", trackingId);
        var response = orderApplicationService.trackOrder(
                TrackOrderQuery.builder()
                        .orderTrackingId(trackingId)
                        .build()
        );
        // System.out.println(response.getOrderStatus()); to be able to compile this line change in:
        // order-service/order-domain/order-application-service/build.gradle
        // from:    implementation project(":common:common-domain") implementation => dependencies we actually don’t want to expose
        // to:      api project(":common:common-domain") api => dependencies that we explicitly want to expose to our consumers.
        log.info("Message='Found order for tracking id', trackingId={}", trackingId);
        return ResponseEntity.ok(response);
    }

}
