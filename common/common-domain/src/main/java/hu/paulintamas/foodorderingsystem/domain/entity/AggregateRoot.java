package hu.paulintamas.foodorderingsystem.domain.entity;

import lombok.experimental.SuperBuilder;

/**
 * [KNOWLEDGE]
 * <p>
 * AGGREGATE A cluster of associated objects that are treated as a unit for the purpose of data changes.
 * External references are restricted to one member of the aggregate, designated as the root.
 * A set of consistency rules applies within the aggregate's boundaries.
 * <p>
 * With other words:
 *  All the work of changing state belongs in the aggregate; stateless domain services provide query support
 *  to the aggregate that is considering a change.
 * <p>
 *  e.g.: business logic orchestration / support queries for the aggregate roots / business logic that works with more aggregates goes to the domain service
 *  e.g.: state changes / validations(consistency rules) goes to the aggregate roots
 */
@SuperBuilder
public abstract class AggregateRoot<ID> extends BaseEntity<ID> {
}
