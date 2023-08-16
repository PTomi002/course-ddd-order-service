package hu.paulintamas.foodorderingsystem.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

/**
 * [KNOWLEDGE]
 * <p>
 * Why to use these ids instead of plain UUID?
 * <p>
 * A: Because it promotes the use of Ubiquitous Language.
 */
@Getter
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class CustomerId extends BaseId<UUID> {
}
