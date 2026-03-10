
# Banking Microservices Technical Test

This project implements a banking system using a microservices architecture.
The solution was built using Java 21 and Spring Boot, following clean architecture principles and good development practices.

The system is composed of two microservices communicating asynchronously through RabbitMQ.

## Architecture

The solution separates the domain into two microservices:

- customer-service → manages clients and authentication
- account-service → manages accounts and transactions

Communication between services is asynchronous using RabbitMQ events.

When a client is created, an event is published and consumed by the account service to maintain a local snapshot.

customer-service  
↓ publish event  
RabbitMQ (bank.exchange)  
↓  
client.created.queue  
↓ consume event  
account-service  
↓  
client_snapshot table

## Technologies Used

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- RabbitMQ
- Docker / Docker Compose
- JWT Authentication
- Swagger / OpenAPI
- JUnit / Mockito

## Project Structure

devsu_technical_test

customer-service → Client management microservice  
account-service → Account and transaction microservice  
infra/docker → Docker infrastructure (DB + RabbitMQ + services)  
postman → Postman collection for API validation

## Running the Project

### Prerequisites

The evaluator only needs:

- Docker Desktop installed
- Git or the project .zip file

No IDE is required.

### Start the system

Navigate to the docker folder:

cd infra/docker

Run:

docker compose up --build

This command will start:

- PostgreSQL
- RabbitMQ
- customer-service
- account-service

## Available Services

Customer Service API  
http://localhost:8081

Account Service API  
http://localhost:8082

## Swagger Documentation

Customer Service  
http://localhost:8081/swagger-ui.html

Account Service  
http://localhost:8082/swagger-ui.html

## RabbitMQ Management Console

RabbitMQ includes a web management console.

URL:
http://localhost:15672

Credentials:
username: guest
password: guest

## RabbitMQ Event Flow

When a client is created:

1. customer-service publishes the event client.created
2. The event is sent to the exchange bank.exchange
3. The message is routed to the queue client.created.queue
4. account-service consumes the event and creates/updates client_snapshot

## Dead Letter Queue (DLQ)

The system includes a Dead Letter Queue for failed message processing.

If an event cannot be processed after retries, it is routed to:

client.created.dlq

This prevents message loss and allows inspection of failed events.

## Database

PostgreSQL runs in Docker.

Connection details:

host: localhost  
port: 5432  
database: bank_db  
username: mend1001  
password: mend1001_2026

Schemas used:

customer_service  
account_service

## Testing the APIs

You can test the endpoints using:

- Swagger UI
- Postman collection included in the project

The Postman collection is located at:

postman/devsu_bank_api_collection.json

You can import this file directly into Postman to test the endpoints.

Example endpoints:

POST /api/clients  
GET /api/accounts  
POST /api/movements

## Unit Testing

The solution includes:

- Unit tests for domain services
- Integration tests for API endpoints

JUnit and Mockito were used.

## Integration Test

The project includes an integration test for the `ClientController`, covering the real HTTP flow for the client management API.

This test validates the interaction between:

- Controller
- Service
- Repository
- Database

The integration test is located at:

customer-service/src/test/java/com/menditech/bank/customer/controller/ClientControllerIntegrationTest.java|
```textmate
Purpose
This test was implemented to satisfy functional requirement F6 – Integration Test.

It verifies that the application can process real HTTP requests and generate the expected responses while interacting with the persistence layer.

Important Notes
The integration test uses real data validations, so the following fields must be unique for each new execution:

identificationNumber

email

If the same values are reused in multiple executions, the test may fail because the application enforces uniqueness constraints in the database.

Recommendation
Before re-running the create client integration test, update these fields in the request body:

identificationNumber

email
```


## Docker Deployment

All components are containerized:

- PostgreSQL
- RabbitMQ
- customer-service
- account-service

This ensures the solution can run consistently in any environment.

## Author

Miguel Angel Mendigaño A

Java developer engineer

