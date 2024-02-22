package hu.paulintamas.foodorderingsystem.saga;

import hu.paulintamas.foodorderingsystem.domain.event.DomainEvent;

/**
 * [KNOWLEDGE-SAGA]
 */
public interface SagaStep<DATA, INPUT extends DomainEvent, ROLLBACK extends DomainEvent> {
    INPUT process(DATA data);
    ROLLBACK rollback(DATA data);
}
