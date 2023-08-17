package hu.paulintamas.foodorderingsystem.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import static lombok.AccessLevel.PRIVATE;

@Getter
@EqualsAndHashCode
@SuperBuilder
@FieldDefaults(makeFinal = true, level = PRIVATE)
public abstract class BaseId<T> {
    private T value;
}
