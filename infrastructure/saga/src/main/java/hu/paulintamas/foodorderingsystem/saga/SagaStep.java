package hu.paulintamas.foodorderingsystem.saga;

/**
 * [KNOWLEDGE-SAGA]
 */
public interface SagaStep<DATA> {
    void process(DATA data);
    void rollback(DATA data);
}
