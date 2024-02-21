package hu.paulintamas.foodorderingsystem.payment.service.domain.entity;

import hu.paulintamas.foodorderingsystem.domain.entity.BaseEntity;
import hu.paulintamas.foodorderingsystem.domain.valueobject.CustomerId;
import hu.paulintamas.foodorderingsystem.domain.valueobject.Money;
import hu.paulintamas.foodorderingsystem.payment.service.domain.valueobject.CreditEntryId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
public class CreditEntry extends BaseEntity<CreditEntryId> {
    private final CustomerId customerId;
    private Money totalCreditAmount;

    public void addCreditAmount(Money money) {
        totalCreditAmount = totalCreditAmount.add(money);
    }

    public void subtractCreditAmount(Money money) {
        totalCreditAmount = totalCreditAmount.subtract(money);
    }

}
