package hu.paulintamas.foodorderingsystem.restaurant.service.domain.valueobject;

import hu.paulintamas.foodorderingsystem.domain.entity.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Getter
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class OrderApprovalId extends BaseEntity<UUID> {
}
