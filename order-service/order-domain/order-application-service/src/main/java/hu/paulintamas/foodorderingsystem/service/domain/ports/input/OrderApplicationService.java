package hu.paulintamas.foodorderingsystem.service.domain.ports.input;

import hu.paulintamas.foodorderingsystem.service.domain.dto.create.CreateOrderCommand;
import hu.paulintamas.foodorderingsystem.service.domain.dto.create.CreateOrderResponse;
import hu.paulintamas.foodorderingsystem.service.domain.dto.track.TrackOrderQuery;
import hu.paulintamas.foodorderingsystem.service.domain.dto.track.TrackOrderResponse;
import jakarta.validation.Valid;

/**
 * [KNOWLEDGE]
 * <p>
 * Input Ports: implemented in the order-application layer.
 * <p>
 *     Used by clients, e.g.: Postman -> Controller
 */
public interface OrderApplicationService {
    CreateOrderResponse createOrder(@Valid CreateOrderCommand createOrderCommand);

    TrackOrderResponse trackOrder(@Valid TrackOrderQuery trackOrderQuery);
}
