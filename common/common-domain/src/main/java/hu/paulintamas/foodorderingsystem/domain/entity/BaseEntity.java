package hu.paulintamas.foodorderingsystem.domain.entity;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * [KNOWLEDGE]
 * <p>
 * Entities (mutable) are equal if they have the same id (properties do not matter).
 * <p>
 * Value objects (immutable) equal if they have the same properties (id does not matter).
 */
@Data
@SuperBuilder
public abstract class BaseEntity<ID> {
    private ID id;
}
