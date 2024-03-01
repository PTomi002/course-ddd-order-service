package hu.paulintamas.foodorderingsystem.saga;

public enum SagaStatus {
    STARTED, // started the order user journey
    FAILED, // failed due to unexpected errors (not business ones)
    SUCCEEDED, // finished the order user journey
    PROCESSING, // processing the payment
    COMPENSATING, // if order is rejected, we have to compensate the user in money return
    COMPENSATED // if order is rejected and we returned the customer's money back
}
