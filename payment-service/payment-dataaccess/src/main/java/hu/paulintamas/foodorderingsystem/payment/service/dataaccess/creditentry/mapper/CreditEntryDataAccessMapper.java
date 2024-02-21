package hu.paulintamas.foodorderingsystem.payment.service.dataaccess.creditentry.mapper;

import hu.paulintamas.foodorderingsystem.domain.valueobject.CustomerId;
import hu.paulintamas.foodorderingsystem.domain.valueobject.Money;
import hu.paulintamas.foodorderingsystem.payment.service.dataaccess.creditentry.entity.CreditEntryEntity;
import hu.paulintamas.foodorderingsystem.payment.service.domain.entity.CreditEntry;
import hu.paulintamas.foodorderingsystem.payment.service.domain.valueobject.CreditEntryId;
import org.springframework.stereotype.Component;

@Component
public class CreditEntryDataAccessMapper {

    public CreditEntry creditEntryEntityToCreditEntry(CreditEntryEntity creditEntryEntity) {
        return CreditEntry.builder()
                .id(CreditEntryId.builder().id(creditEntryEntity.getId()).build())
                .customerId(CustomerId.builder().value(creditEntryEntity.getCustomerId()).build())
                .totalCreditAmount(Money.of(creditEntryEntity.getTotalCreditAmount()))
                .build();
    }

    public CreditEntryEntity creditEntryToCreditEntryEntity(CreditEntry creditEntry) {
        return CreditEntryEntity.builder()
                .id(creditEntry.getId().getId())
                .customerId(creditEntry.getCustomerId().getValue())
                .totalCreditAmount(creditEntry.getTotalCreditAmount().getAmount())
                .build();
    }

}
