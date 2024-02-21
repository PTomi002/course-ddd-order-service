package hu.paulintamas.foodorderingsystem.payment.service.domain.valueobject;

public enum TransactionType {
    DEBIT, // subtract money from account
    CREDIT; // add money to account
}
