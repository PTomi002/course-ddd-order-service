package hu.paulintamas.foodorderingsystem.outbox;

public interface OutboxScheduler {
    void processOutboxMessage();
}
