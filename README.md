# Domain Driven Design - Food Ordering System

## Repository Description
This project is a fictive food ordering system written in Java - Spring applied with the following architecture patterns and project structures:
```bash
- monorepo structure (gradle multimodule)
- Domain Driven Design
- Event Sourcing (created domain events only for simplicity)
- CQRS (read update flow for customer)
- Saga (compensating messages)
- Outbox (save entities and events in a local ACID transaction)
```

## Tools

Different tools to debug the repository and make visualisation easier.

### graphfity

```bash
./gradlew graphfity
```