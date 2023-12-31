package hu.paulintamas.foodorderingsystem.domain.event;

/**
 * [KNOWLEDGE]
 * <p>
 * Describe things that happen and change the state of a domain.
 * <p>
 * Domain event listeners runs in a different transaction than the event publishers.
 * <p>
 * Domain events are an excellent way of achieving eventual consistency, for strong consistency use direct method calls.
 */
public interface DomainEvent<T> {

}
