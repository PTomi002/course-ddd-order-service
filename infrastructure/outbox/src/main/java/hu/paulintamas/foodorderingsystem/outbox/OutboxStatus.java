package hu.paulintamas.foodorderingsystem.outbox;

public enum OutboxStatus {
    STARTED,
    COMPLETED,
    FAILED // only if unexpected kafka error happens
}
