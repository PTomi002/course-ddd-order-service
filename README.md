# Domain Driven Design - Food Ordering System

## Repository Description
This project is a fictive food ordering system written in Java - Spring applied with the following architecture patterns and project structures:
```bash
- monorepo structure (gradle multimodule)
- Domain Driven Design
- Event Sourcing
- CQRS
- Saga
- Outbox
```

## Tools

Different tools to debug the repository and make visualisation easier.

### graphfity
```bash
./gradlew graphfity
```

### CQRS
#### Happy Flow
```bash
curl --location --request POST 'http://localhost:8182/customers' \
--header 'Content-Type: application/json' \
--data-raw '{
  "customerId": "d215b5f8-0249-4dc5-89a3-51fd148cfb41",
  "username": "user_1",
  "firstName": "First",
  "lastName": "User"
}'
```

### Testing
Basic error and happy flows as e2e manual tests.

#### Not Enough Credit
```bash
curl --location --request POST 'http://localhost:8181/v1/orders' \
--header 'Content-Type: application/json' \
--data-raw '{
    "customerId": "d215b5f8-0249-4dc5-89a3-51fd148cfb41",
    "restaurantId": "d215b5f8-0249-4dc5-89a3-51fd148cfb45",
    "address": {
        "street": "street_1",
        "postalCode": "1000AB",
        "city": "Amsterdam"
    },
    "price": 550.00,
    "items": [
        {
            "productId": "d215b5f8-0249-4dc5-89a3-51fd148cfb48",
            "quantity": 1,
            "price": 50.00,
            "subTotal": 50.00
        },
        {
            "productId": "d215b5f8-0249-4dc5-89a3-51fd148cfb48",
            "quantity": 10,
            "price": 50.00,
            "subTotal": 500.00
        }
    ]
}'
```

#### No Product Found With Id
```bash
curl --location --request POST 'http://localhost:8181/v1/orders' \
--header 'Content-Type: application/json' \
--data-raw '{
    "customerId": "d215b5f8-0249-4dc5-89a3-51fd148cfb41",
    "restaurantId": "d215b5f8-0249-4dc5-89a3-51fd148cfb45",
    "address": {
        "street": "street_1",
        "postalCode": "1000AB",
        "city": "Amsterdam"
    },
    "price": 25.00,
    "items": [
        {
            "productId": "d215b5f8-0249-4dc5-89a3-51fd148cfb47",
            "quantity": 1,
            "price": 25.00,
            "subTotal": 25.00
        }
    ]
}'
```

#### Happy Flow
```bash
{
    "customerId": "d215b5f8-0249-4dc5-89a3-51fd148cfb41",
    "restaurantId": "d215b5f8-0249-4dc5-89a3-51fd148cfb45",
    "address": {
        "street": "street_1",
        "postalCode": "1000AB",
        "city": "Amsterdam"
    },
    "price": 200.00,
    "items": [
        {
            "productId": "d215b5f8-0249-4dc5-89a3-51fd148cfb48",
            "quantity": 1,
            "price": 50.00,
            "subTotal": 50.00
        },
        {
            "productId": "d215b5f8-0249-4dc5-89a3-51fd148cfb48",
            "quantity": 3,
            "price": 50.00,
            "subTotal": 150.00
        }
    ]
}

curl --location --request GET 'http://localhost:8181/v1/orders/8506f492-7ede-41f7-97be-1f0c5a5644f4'
```
