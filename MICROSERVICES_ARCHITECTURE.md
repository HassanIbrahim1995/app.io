# Hotel Management System - Microservices Architecture

## Overview
The hotel management system has been refactored from a monolithic application into a microservices architecture for better scalability, maintainability, and independent deployment.

## Architecture Diagram

```
                                    ┌─────────────────┐
                                    │   API Gateway   │
                                    │   (Port 8080)   │
                                    └────────┬────────┘
                                             │
                    ┌────────────────────────┼────────────────────────┐
                    │                        │                        │
           ┌────────▼────────┐      ┌───────▼────────┐      ┌───────▼────────┐
           │ Service Discovery│      │  Config Server │      │  Message Broker│
           │    (Eureka)      │      │   (Port 8888)  │      │  (RabbitMQ)    │
           │   (Port 8761)    │      └────────────────┘      └────────────────┘
           └──────────────────┘
                    │
        ┌───────────┼───────────────────────────────────────────┐
        │           │                                           │
   ┌────▼─────┐ ┌──▼──────┐ ┌──────────┐ ┌──────────┐ ┌──────▼──────┐
   │  User    │ │  Guest  │ │   Room   │ │Reservation│ │  Payment    │
   │ Service  │ │ Service │ │ Service  │ │ Service   │ │  Service    │
   │ (8081)   │ │ (8082)  │ │  (8083)  │ │  (8084)   │ │  (8085)     │
   └──────────┘ └─────────┘ └──────────┘ └───────────┘ └─────────────┘
        │
   ┌────┼─────────────────────────────────────────────────────┐
   │    │                                                      │
┌──▼────────┐ ┌────────────┐ ┌──────────┐ ┌──────────┐ ┌────▼──────┐
│Housekeeping│ │  Product   │ │  Policy  │ │  Review  │ │ Analytics │
│  Service   │ │  Service   │ │ Service  │ │ Service  │ │  Service  │
│  (8086)    │ │  (8087)    │ │  (8088)  │ │  (8089)  │ │  (8090)   │
└────────────┘ └────────────┘ └──────────┘ └──────────┘ └───────────┘
```

## Microservices

### 1. **API Gateway Service** (Port 8080)
**Responsibility**: Entry point for all client requests
- Route requests to appropriate microservices
- Load balancing
- Authentication and authorization
- Rate limiting
- Request/response transformation
- Circuit breaker implementation

**Technology**: Spring Cloud Gateway

### 2. **Service Discovery** (Port 8761)
**Responsibility**: Service registration and discovery
- Dynamic service registration
- Health checking
- Load balancing information
- Service metadata management

**Technology**: Netflix Eureka

### 3. **Config Server** (Port 8888)
**Responsibility**: Centralized configuration management
- External configuration for all services
- Environment-specific configurations
- Dynamic configuration updates
- Encrypted secrets management

**Technology**: Spring Cloud Config

### 4. **User Service** (Port 8081)
**Responsibility**: User authentication and management
- User registration and login
- JWT token generation and validation
- Password management
- Employee management
- Role and permission management
- Session management

**Database**: user_db (PostgreSQL)

**APIs**:
- POST /api/v1/auth/register
- POST /api/v1/auth/login
- POST /api/v1/auth/refresh
- GET /api/v1/users/{id}
- PUT /api/v1/users/{id}
- GET /api/v1/employees
- POST /api/v1/employees

### 5. **Guest Service** (Port 8082)
**Responsibility**: Guest profile and preference management
- Guest profile CRUD
- Guest preferences
- Loyalty program management
- Guest history
- Document management

**Database**: guest_db (PostgreSQL)

**APIs**:
- GET /api/v1/guests/{id}
- PUT /api/v1/guests/{id}
- GET /api/v1/guests/{id}/preferences
- POST /api/v1/guests/{id}/preferences
- GET /api/v1/guests/{id}/loyalty

### 6. **Room Service** (Port 8083)
**Responsibility**: Room and inventory management
- Room CRUD
- Room types and amenities
- Room availability checking
- Room status management
- Room pricing

**Database**: room_db (PostgreSQL)

**APIs**:
- GET /api/v1/rooms
- GET /api/v1/rooms/{id}
- POST /api/v1/rooms
- PUT /api/v1/rooms/{id}
- GET /api/v1/rooms/available
- GET /api/v1/room-types

### 7. **Reservation Service** (Port 8084)
**Responsibility**: Booking and reservation management
- Reservation creation and management
- Check-in/check-out processing
- Reservation cancellation
- Group reservations
- Room moves
- Date overlap validation

**Database**: reservation_db (PostgreSQL)

**APIs**:
- POST /api/v1/reservations
- GET /api/v1/reservations/{id}
- PUT /api/v1/reservations/{id}
- POST /api/v1/reservations/{id}/cancel
- POST /api/v1/reservations/{id}/checkin
- POST /api/v1/reservations/{id}/checkout
- POST /api/v1/group-reservations

**Events Published**:
- ReservationCreated
- ReservationCancelled
- CheckInCompleted
- CheckOutCompleted

### 8. **Payment Service** (Port 8085)
**Responsibility**: Payment processing and revenue management
- Payment processing
- Refund management
- Revenue tracking
- Invoice generation
- Payment method management

**Database**: payment_db (PostgreSQL)

**APIs**:
- POST /api/v1/payments
- GET /api/v1/payments/{id}
- POST /api/v1/payments/{id}/refund
- GET /api/v1/revenues
- GET /api/v1/revenues/analytics

**Events Consumed**:
- ReservationCreated
- ReservationCancelled

### 9. **Housekeeping Service** (Port 8086)
**Responsibility**: Housekeeping task management
- Task creation and assignment
- Room cleaning status
- Task completion tracking
- Staff scheduling

**Database**: housekeeping_db (PostgreSQL)

**APIs**:
- GET /api/v1/housekeeping/tasks
- POST /api/v1/housekeeping/tasks
- PUT /api/v1/housekeeping/tasks/{id}
- POST /api/v1/housekeeping/tasks/{id}/assign
- POST /api/v1/housekeeping/tasks/{id}/complete

**Events Consumed**:
- CheckOutCompleted

### 10. **Product Service** (Port 8087)
**Responsibility**: Product catalog and inventory
- Product CRUD
- Inventory management
- Upselling recommendations
- Product sales
- Stock alerts

**Database**: product_db (PostgreSQL)

**APIs**:
- GET /api/v1/products
- POST /api/v1/products
- PUT /api/v1/products/{id}
- POST /api/v1/products/{id}/purchase
- GET /api/v1/upsells

### 11. **Policy Service** (Port 8088)
**Responsibility**: Policy and rate plan management
- Hotel policies
- Rate plans
- Cancellation policies
- Policy acceptance tracking
- City tax and fee management

**Database**: policy_db (PostgreSQL)

**APIs**:
- GET /api/v1/policies
- POST /api/v1/policies
- GET /api/v1/rate-plans
- POST /api/v1/rate-plans
- POST /api/v1/policies/{id}/accept

### 12. **Review Service** (Port 8089)
**Responsibility**: Review and rating management
- Review creation
- Rating management
- Management responses
- Review analytics

**Database**: review_db (PostgreSQL)

**APIs**:
- GET /api/v1/reviews
- POST /api/v1/reviews
- PUT /api/v1/reviews/{id}
- POST /api/v1/reviews/{id}/respond

**Events Consumed**:
- CheckOutCompleted

### 13. **Analytics Service** (Port 8090)
**Responsibility**: Reporting and business intelligence
- Revenue analytics
- Occupancy reports
- Guest analytics
- Performance metrics
- Dashboard data aggregation

**Database**: analytics_db (PostgreSQL)

**APIs**:
- GET /api/v1/analytics/revenue
- GET /api/v1/analytics/occupancy
- GET /api/v1/analytics/guests
- GET /api/v1/analytics/dashboard

## Inter-Service Communication

### Synchronous Communication (REST)
- API Gateway routes external requests
- Services call each other via REST APIs when immediate response needed
- Circuit breakers implemented with Resilience4j
- Retry policies and fallback mechanisms

### Asynchronous Communication (Message Broker)
- RabbitMQ for event-driven communication
- Event publishing for loosely coupled services
- Event topics:
  - reservation.created
  - reservation.cancelled
  - checkout.completed
  - payment.processed
  - payment.failed

### Service-to-Service Authentication
- JWT tokens passed through headers
- Service accounts for internal communication
- API key authentication for service-to-service calls

## Data Management

### Database Per Service Pattern
Each microservice has its own database to ensure loose coupling:
- user_db
- guest_db
- room_db
- reservation_db
- payment_db
- housekeeping_db
- product_db
- policy_db
- review_db
- analytics_db

### Shared Database Concerns
- Audit logs stored in centralized audit_db
- Read-only replicas for analytics service

### Data Consistency
- Saga pattern for distributed transactions
- Eventual consistency model
- Compensating transactions for rollbacks
- Event sourcing for critical business events

## Configuration Management

### Config Server Structure
```
config-repo/
├── application.yml              # Common configuration
├── application-dev.yml          # Dev environment
├── application-prod.yml         # Production environment
├── user-service.yml
├── guest-service.yml
├── room-service.yml
├── reservation-service.yml
├── payment-service.yml
├── housekeeping-service.yml
├── product-service.yml
├── policy-service.yml
├── review-service.yml
└── analytics-service.yml
```

### Configuration Properties
- Database connections
- Service URLs
- Message broker configuration
- Security settings
- Feature flags
- Rate limiting rules

## Security

### Authentication Flow
1. Client sends credentials to API Gateway
2. Gateway forwards to User Service
3. User Service validates and returns JWT
4. Client includes JWT in subsequent requests
5. Gateway validates JWT
6. Gateway routes to appropriate service with service token

### Authorization
- Role-based access control (RBAC)
- Service-level permissions
- Gateway enforces authentication
- Each service validates authorization for its resources

### Security Measures
- TLS/SSL for all communications
- Secrets encrypted in Config Server
- API keys for service-to-service
- Rate limiting at Gateway
- DDoS protection

## Observability

### Logging
- Centralized logging with ELK Stack (Elasticsearch, Logstash, Kibana)
- Structured JSON logging
- Correlation IDs for request tracing
- Log levels: ERROR, WARN, INFO, DEBUG

### Monitoring
- Prometheus for metrics collection
- Grafana for visualization
- Spring Boot Actuator endpoints
- Health checks for all services

### Distributed Tracing
- Spring Cloud Sleuth for trace IDs
- Zipkin for distributed tracing
- Request flow visualization
- Performance bottleneck identification

## Resilience Patterns

### Circuit Breaker
- Implemented with Resilience4j
- Prevents cascading failures
- Fallback mechanisms
- Automatic recovery

### Retry Logic
- Exponential backoff
- Maximum retry attempts
- Idempotent operations

### Bulkhead
- Thread pool isolation
- Resource limiting
- Service protection

### Rate Limiting
- Per-user rate limits
- Per-service rate limits
- Burst handling

## Deployment

### Docker Compose (Development)
```yaml
services:
  - eureka-server
  - config-server
  - api-gateway
  - user-service
  - guest-service
  - room-service
  - reservation-service
  - payment-service
  - housekeeping-service
  - product-service
  - policy-service
  - review-service
  - analytics-service
  - rabbitmq
  - postgresql (multiple instances)
  - redis
```

### Kubernetes (Production)
- Separate deployments for each service
- Service mesh with Istio (optional)
- Horizontal Pod Autoscaling (HPA)
- ConfigMaps and Secrets
- Persistent volumes for databases
- Ingress controller for external access

## Migration Strategy

### Phase 1: Infrastructure Setup
1. Deploy Service Discovery (Eureka)
2. Deploy Config Server
3. Deploy API Gateway
4. Deploy Message Broker (RabbitMQ)

### Phase 2: Extract Services
1. Extract User Service (most independent)
2. Extract Room Service
3. Extract Guest Service
4. Extract Reservation Service (depends on Room & Guest)
5. Extract Payment Service
6. Extract remaining services

### Phase 3: Refactor Communication
1. Implement REST clients
2. Add message publishing
3. Add event consumers
4. Remove direct database access between services

### Phase 4: Data Migration
1. Separate databases
2. Migrate data to service-specific databases
3. Remove cross-database queries

## Benefits

### Scalability
- Scale services independently based on load
- Room Service can scale during high search traffic
- Payment Service can scale during checkout times

### Resilience
- Service failures don't bring down entire system
- Circuit breakers prevent cascading failures
- Graceful degradation

### Development Velocity
- Teams can work on services independently
- Deploy services independently
- Technology diversity (different languages/frameworks per service)

### Maintainability
- Smaller, focused codebases
- Easier to understand and modify
- Clear service boundaries

## Challenges and Solutions

### Distributed Transactions
- **Challenge**: Maintaining data consistency across services
- **Solution**: Saga pattern with compensating transactions

### Service Discovery
- **Challenge**: Dynamic service locations
- **Solution**: Eureka for automatic registration and discovery

### Debugging
- **Challenge**: Tracing requests across services
- **Solution**: Distributed tracing with Sleuth and Zipkin

### Testing
- **Challenge**: Integration testing across services
- **Solution**: Contract testing, service virtualization

## Best Practices

1. **API Versioning**: Use URL versioning (v1, v2)
2. **Backward Compatibility**: Don't break existing clients
3. **Documentation**: OpenAPI/Swagger for all services
4. **Health Checks**: Implement liveness and readiness probes
5. **Graceful Shutdown**: Handle in-flight requests
6. **Idempotency**: All operations should be idempotent
7. **Correlation IDs**: Track requests across services
8. **Timeouts**: Set appropriate timeouts for all calls
9. **Caching**: Use Redis for frequently accessed data
10. **Monitoring**: Monitor everything that matters

## Technology Stack

- **Framework**: Spring Boot 3.x, Spring Cloud
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Config Management**: Spring Cloud Config
- **Message Broker**: RabbitMQ
- **Databases**: PostgreSQL (per service)
- **Caching**: Redis
- **Monitoring**: Prometheus + Grafana
- **Tracing**: Zipkin
- **Logging**: ELK Stack
- **Container**: Docker
- **Orchestration**: Kubernetes
- **CI/CD**: Jenkins/GitLab CI
