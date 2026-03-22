# Empik Discount Coupon Service

A Spring Boot-based microservice for managing and redeeming discount coupons with GeoIP validation and usage limit tracking

## A few words of explanation

Based on the provided requirements, I proposed a solution that I consider optimal from my perspective.
In a real-world project, I would need answers to several additional questions,
such as: what level of traffic is expected, and what type of coupons will be handled (e.g., high-demand “hot” coupons with traffic spikes, or rather steady and evenly distributed usage over time).

### Locking strategy for redeeming coupons:

Pessimistic locking has been chosen as the preferred strategy
for handling concurrent data access in this implementation.

The main reason for this decision is the need to avoid wasted work
caused by concurrent update conflicts. While optimistic locking
performs well under low contention, frequent collisions would result
in repeated retries (retries for ObjectOptimisticLockingFailureException),
increasing system overhead and reducing efficiency.

Although pessimistic locking may introduce a bottleneck under a high load,
it ensures that only one transaction modifies the data at a time.
This shifts the cost from retrying operations to waiting (or timing out),
which is more predictable and easier to control from a system perspective.

Atomic updates were also considered, as they scale well,
but they would require moving domain logic into the database layer,
increasing complexity and reducing maintainability.

Based on the current business requirements and unknown traffic patterns,
pessimistic locking provides the best balance.

### GeoIP validation:

When we are unable to fetch the result, a decision must be made on how the system should behave.
Currently, to remain aligned with the business requirements, such requests are rejected.

However, from a practical perspective, it would be reasonable to log the error and proceed using
an “unknown” country, allowing the user to activate the coupon.



## Tech Stack & Libraries

- **Java 25**
- **Spring Boot 4.4**
- **PostgreSQL 17**
- **Flyway**
- **Spring Cache**
- **Micrometer/Prometheus**
- **Docker & Docker Compose**

## Architecture

The project uses a simplified Hexagonal Architecture with one module `coupon`:

- `domain`: Core business entities, use cases (application layer), and port interfaces.
- `infrastructure`: Adapters/ports implemtations.
- `api`: REST API controller and HTTP DTOs.

## How to Run

### Prerequisites
- Docker & Docker Compose
- JDK 25
- Maven

### Starting the Application (Docker Compose)

To start the database and the application together:
```bash
docker-compose up
```
The API will be available at `http://localhost:8080`.

### Running with a Local Database (Dev Mode)
If you want to run the app from your IDE:
1. Start the PostgreSQL container only:
   ```bash
   docker-compose -f docker-compose-dev.yaml up
   ```
2. Run the application via Maven:
   ```bash
   mvn spring-boot:run
   ```

## Testing

### Running All Tests
```bash
docker-compose -f docker-compose-test.yaml up -d
mvn test
docker-compose -f docker-compose-test.yaml down
```

## Build

To package the application into a runnable JAR:
```bash
mvn clean package -DskipTests
```

## Monitoring

- **Health Check**: `GET /actuator/health`
- **Metrics**: `GET /actuator/prometheus`

### API

Example IPS:
- PL `89.64.112.7`
- DE `77.1.2.3`

```bash
curl -X POST http://localhost:8080/api/coupons \
     -H "Content-Type: application/json" \
     -d '{
       "code": "WIOSNA20",
       "country": "PL",
       "maxUsage": 100
     }'
```

```bash
curl -X POST http://localhost:8080/api/coupons/redeem \
     -H "Content-Type: application/json" \
     -H "X-Forwarded-For: 89.64.112.7" \
     -d '{
       "couponCode": "WIOSNA20",
       "userId": "550e8400-e29b-41d4-a716-446655440000"
     }'
```
