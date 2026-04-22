# Notification Service (ms-lending-app-notification-service)

Event-driven notification service that consumes Kafka events from Loan and Customer services, resolves notification rules, applies customer preferences, renders templates, and dispatches notifications via email, SMS, or push.

## Tech Stack

| Component        | Technology                              |
|------------------|-----------------------------------------|
| Framework        | Spring Boot 3.4.4 / Spring WebFlux      |
| Language         | Java 21                                 |
| Database         | PostgreSQL (R2DBC — reactive)           |
| Migrations       | Flyway (runs over JDBC at startup)      |
| Event Broker     | Apache Kafka (consumes `lending.loan.events`, `lending.customer.events`) |
| Security         | API Key (`X-API-KEY` header)            |
| Testing          | JUnit 5 + Mockito + StepVerifier        |

## Prerequisites

- Java 21+
- Maven 3.9+
- Docker + Docker Compose (recommended for local stack)
- Loan Service and Customer Service publishing events

## Run With Docker Compose (Single Broker + Consumer)

Use one compose stack to run exactly one PostgreSQL instance, one Kafka broker, and one notification-service consumer.

```bash
cd ms-lending-app-notification-service

# Build and start all services
docker compose up -d --build

# View service logs
docker compose logs -f notification-service

# Stop all services
docker compose down
```

Notes:
- Kafka is exposed on `localhost:9092` for other local producers.
- Notification service runs on `localhost:8084`.
- PostgreSQL runs on `localhost:5432` (database: `lending_notification_db`).

## Getting Started

```bash
cd ms-lending-app-notification-service

# Build
mvn clean compile

# Run
mvn spring-boot:run

# Run tests
mvn clean test
```

The service starts on **port 8084** and Flyway auto-creates all tables on first startup.

## Configuration

| Property                              | Default                                                    |
|---------------------------------------|------------------------------------------------------------|
| `server.port`                         | `8084`                                                     |
| `spring.r2dbc.url`                    | `r2dbc:postgresql://localhost:5432/lending_notification_db` |
| `app.security.api-key`               | `notification-service-api-key-2024`                        |
| `spring.kafka.bootstrap-servers`      | `localhost:9092`                                           |
| `spring.kafka.consumer.group-id`      | `notification-service-group`                               |

## Database Schema

Flyway migration `V1__init_schema.sql` creates:

- **notifications** — dispatched notification records (customer, loan, event type, channel, status, body)
- **notification_templates** — reusable templates per event type + channel with `{{variable}}` placeholders
- **notification_rules** — configurable rules per product/segment/event (which channels to send)
- **customer_notification_preferences** — per-customer channel opt-in/out

`V2__seed_data.sql` inserts templates for all event types (EMAIL + SMS) and default notification rules.

## Notification Pipeline

```
Kafka Event ──▶ NotificationEventConsumer
                    │
                    ▼
            Resolve Rules (event type → channels)
                    │
                    ▼
            Check Customer Preferences (channel enabled?)
                    │
                    ▼
            Find Template (event type + channel)
                    │
                    ▼
            Render Template (substitute {{variables}})
                    │
                    ▼
            Send customized notofication via channel API (EMAIL/SMS/PUSH)
                    │
                    ▼
            Save Notification + Dispatch (EMAIL/SMS/PUSH)
```

## Kafka Events Consumed

| Topic                     | Event Types                                                                  |
|---------------------------|------------------------------------------------------------------------------|
| `lending.loan.events`     | LOAN_CREATED, REPAYMENT_RECEIVED, LOAN_CLOSED, LOAN_CANCELLED, OVERDUE_NOTICE |
| `lending.customer.events` | LIMIT_UPDATED                                                                |

Consumer config: `concurrency=3`, `CooperativeStickyAssignor`, `MANUAL_IMMEDIATE` ack mode.

## API Endpoints

All endpoints require header: `X-API-KEY: notification-service-api-key-2024`

| Method | Endpoint                                         | Description                        |
|--------|--------------------------------------------------|------------------------------------|
| `GET`  | `/api/v1/notifications/customer/{customerId}`    | Get notifications for customer     |
| `GET`  | `/api/v1/notifications/loan/{loanId}`            | Get notifications for loan         |
| `GET`  | `/api/v1/notifications/templates`                | List all notification templates    |

## Template Variables

Templates use `{{variable}}` syntax. Available variables from events:

| Variable         | Description                |
|------------------|----------------------------|
| `{{customerName}}` | Customer full name        |
| `{{loanAmount}}`   | Loan/repayment amount     |
| `{{dueDate}}`      | Payment due date          |
| `{{productName}}`  | Loan product name         |
| `{{loanId}}`       | Loan identifier           |

## Example — Query Customer Notifications

```bash
curl http://localhost:8084/api/v1/notifications/customer/d1e2f3a4-b5c6-7890-def1-234567890abc \
  -H "X-API-KEY: notification-service-api-key-2024"
```

## Project Structure

```
src/main/java/com/glo/lending/notification/
├── NotificationServiceApplication.java
├── component/
│   └── NotificationEventConsumer.java     # Kafka consumer for loan + customer events
├── config/
│   ├── KafkaConsumerConfig.java           # Concurrency=3, CooperativeSticky
│   └── SecurityConfig.java
├── controller/
│   └── NotificationController.java
├── model/
│   ├── dto/                               # NotificationEvent, NotificationResponse, TemplateRequest
│   └── enums/                             # NotificationChannel, NotificationEventType, NotificationStatus
├── dblayer/
│   ├── entities/                          # Notification, NotificationTemplate, NotificationRule, CustomerNotificationPreference
│   └── repo/                              # Reactive repositories
└── service/
│   ├── dipatcher/              # Load + render templates
    └── NotificationService.java           # Rule resolution, template rendering, dispatch
```

