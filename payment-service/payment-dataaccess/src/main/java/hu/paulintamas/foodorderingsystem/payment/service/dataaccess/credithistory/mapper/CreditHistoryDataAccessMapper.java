package hu.paulintamas.foodorderingsystem.payment.service.dataaccess.credithistory.mapper;

import hu.paulintamas.foodorderingsystem.domain.valueobject.CustomerId;
import hu.paulintamas.foodorderingsystem.domain.valueobject.Money;
import hu.paulintamas.foodorderingsystem.payment.service.dataaccess.credithistory.entity.CreditHistoryEntity;
import hu.paulintamas.foodorderingsystem.payment.service.domain.entity.CreditHistory;
import hu.paulintamas.foodorderingsystem.payment.service.domain.valueobject.CreditHistoryId;
import org.springframework.stereotype.Component;

@Component
public class CreditHistoryDataAccessMapper {

    public CreditHistory creditHistoryEntityToCreditHistory(CreditHistoryEntity creditHistoryEntity) {
        return CreditHistory.builder()
                .id(CreditHistoryId.builder().id(creditHistoryEntity.getId()).build())
                .customerId(CustomerId.builder().value(creditHistoryEntity.getCustomerId()).build())
                .amount(Money.of(creditHistoryEntity.getAmount()))
                .transactionType(creditHistoryEntity.getType())
                .build();
    }

    public CreditHistoryEntity creditHistoryToCreditHistoryEntity(CreditHistory creditHistory) {
        return CreditHistoryEntity.builder()
                .id(creditHistory.getId().getId())
                .customerId(creditHistory.getCustomerId().getValue())
                .amount(creditHistory.getAmount().getAmount())
                .type(creditHistory.getTransactionType())
                .build();
    }

}
