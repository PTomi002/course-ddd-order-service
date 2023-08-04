package hu.paulintamas.foodorderingsystem.service.domain.valueobject;

import hu.paulintamas.foodorderingsystem.domain.valueobject.BaseId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import static lombok.AccessLevel.PRIVATE;

@Getter
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class OrderItemId extends BaseId<Long> {
}
