# Migration Guide: Monolith to Microservices

## Overview
This guide provides step-by-step instructions for migrating the Hotel Management System from a monolithic architecture to microservices.

## Prerequisites
- Docker and Docker Compose installed
- Maven 3.8+
- Java 17+
- Access to source code repository
- Database backup of existing data

## Migration Phases

### Phase 1: Infrastructure Setup (Week 1)

#### 1.1 Deploy Service Discovery
```bash
cd microservices/eureka-server
mvn clean package
docker build -t hotel-eureka-server .
docker run -p 8761:8761 hotel-eureka-server
```

Verify: http://localhost:8761

#### 1.2 Deploy Config Server
```bash
cd microservices/config-server
mvn clean package
docker build -t hotel-config-server .
docker run -p 8888:8888 hotel-config-server
```

Verify: http://localhost:8888/actuator/health

#### 1.3 Deploy API Gateway
```bash
cd microservices/api-gateway
mvn clean package
docker build -t hotel-api-gateway .
docker run -p 8080:8080 hotel-api-gateway
```

Verify: http://localhost:8080/actuator/health

#### 1.4 Setup Message Broker
```bash
docker run -d --name rabbitmq \
  -p 5672:5672 -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=hotel \
  -e RABBITMQ_DEFAULT_PASS=hotel123 \
  rabbitmq:3-management-alpine
```

Verify: http://localhost:15672 (hotel/hotel123)

### Phase 2: Extract User Service (Week 2)

#### 2.1 Create Database
```sql
CREATE DATABASE user_db;
```

#### 2.2 Copy Entities
Extract from monolith:
- User
- Employee
- Enums (UserRole, EmploymentType, EmploymentStatus)

#### 2.3 Copy Repositories
- UserRepository
- EmployeeRepository

#### 2.4 Copy Services
- AuthService
- EmployeeService

#### 2.5 Copy Controllers
- AuthController
- EmployeeController

#### 2.6 Add Dependencies
```xml
<!-- Eureka Client -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

#### 2.7 Configure
```yaml
spring:
  application:
    name: user-service
  datasource:
    url: jdbc:postgresql://localhost:5432/user_db
    
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

#### 2.8 Test
```bash
mvn clean package
java -jar target/user-service-1.0.0.jar
```

Test endpoints through Gateway:
```bash
curl http://localhost:8080/api/v1/auth/login
```

### Phase 3: Extract Room Service (Week 3)

#### 3.1 Create Database
```sql
CREATE DATABASE room_db;
```

#### 3.2 Extract Components
- Room, RoomType, Amenity entities
- RoomRepository, RoomTypeRepository, AmenityRepository
- RoomService
- RoomController

#### 3.3 Add Redis for Caching
```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

#### 3.4 Deploy and Test

### Phase 4: Extract Guest Service (Week 3)

#### 4.1 Create Database
```sql
CREATE DATABASE guest_db;
```

#### 4.2 Extract Components
- Guest, GuestPreference entities
- GuestRepository
- GuestProfileService
- GuestProfileController

#### 4.3 Create REST Client for User Service
```java
@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/api/v1/users/{id}")
    UserResponse getUserById(@PathVariable Long id);
}
```

### Phase 5: Extract Reservation Service (Week 4)

#### 5.1 Create Database
```sql
CREATE DATABASE reservation_db;
```

#### 5.2 Extract Components
- Reservation, GroupReservation, RoomMoveHistory entities
- ReservationRepository
- ReservationService, GroupReservationService, RoomMoveService
- ReservationController

#### 5.3 Setup Event Publishing
```java
@Service
public class ReservationEventPublisher {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void publishReservationCreated(Reservation reservation) {
        rabbitTemplate.convertAndSend(
            "reservation.exchange",
            "reservation.created",
            toEvent(reservation)
        );
    }
}
```

#### 5.4 Add Feign Clients
```java
@FeignClient(name = "room-service")
public interface RoomServiceClient {
    @GetMapping("/api/v1/rooms/{id}")
    RoomResponse getRoomById(@PathVariable Long id);
    
    @GetMapping("/api/v1/rooms/{id}/available")
    Boolean isRoomAvailable(@PathVariable Long id, 
                           @RequestParam LocalDate checkIn,
                           @RequestParam LocalDate checkOut);
}

@FeignClient(name = "guest-service")
public interface GuestServiceClient {
    @GetMapping("/api/v1/guests/{id}")
    GuestResponse getGuestById(@PathVariable Long id);
}
```

### Phase 6: Extract Payment Service (Week 5)

#### 6.1 Create Database
```sql
CREATE DATABASE payment_db;
```

#### 6.2 Extract Components
- Payment, Revenue entities
- PaymentRepository, RevenueRepository
- PaymentService, RevenueService
- PaymentController, RevenueController

#### 6.3 Setup Event Consumers
```java
@RabbitListener(queues = "reservation.created.queue")
public void handleReservationCreated(ReservationCreatedEvent event) {
    // Create pending payment
    createPaymentForReservation(event);
}

@RabbitListener(queues = "reservation.cancelled.queue")
public void handleReservationCancelled(ReservationCancelledEvent event) {
    // Process refund
    processRefund(event);
}
```

### Phase 7: Extract Remaining Services (Week 6-7)

#### 7.1 Housekeeping Service
- Extract housekeeping entities and services
- Setup event consumer for checkout events

#### 7.2 Product Service
- Extract product catalog
- Inventory management

#### 7.3 Policy Service
- Extract policies and rate plans
- City tax and fees

#### 7.4 Review Service
- Extract review entities
- Setup event consumer for checkout

#### 7.5 Analytics Service
- Aggregate data from other services
- Reporting and dashboards
- Centralized audit logs

### Phase 8: Data Migration (Week 8)

#### 8.1 Export Data from Monolith
```bash
pg_dump -t users -t employees monolith_db > users_export.sql
pg_dump -t guests -t guest_preferences monolith_db > guests_export.sql
# Repeat for all tables
```

#### 8.2 Import Data to Microservices
```bash
psql user_db < users_export.sql
psql guest_db < guests_export.sql
# Repeat for all services
```

#### 8.3 Verify Data Integrity
Run consistency checks:
```sql
SELECT COUNT(*) FROM users; -- Should match monolith
SELECT COUNT(*) FROM reservations; -- Should match monolith
```

### Phase 9: Switch Traffic (Week 9)

#### 9.1 Parallel Running
- Run both monolith and microservices
- Route read traffic to microservices
- Keep writes on monolith
- Compare results

#### 9.2 Gradual Cutover
- Start with 10% traffic to microservices
- Increase to 25%, 50%, 75%, 100%
- Monitor errors and performance
- Rollback plan ready

#### 9.3 DNS Switching
```bash
# Update DNS to point to API Gateway
api.hotel.com -> api-gateway:8080
```

### Phase 10: Decommission Monolith (Week 10)

#### 10.1 Final Data Sync
- Ensure all data migrated
- Archive monolith database

#### 10.2 Shutdown Monolith
```bash
docker stop hotel-monolith
```

#### 10.3 Monitor
- Watch for any issues
- Keep monolith ready for 2 weeks

## Testing Strategy

### Unit Tests
```java
@SpringBootTest
class ReservationServiceTest {
    
    @MockBean
    private RoomServiceClient roomServiceClient;
    
    @Test
    void testCreateReservation() {
        when(roomServiceClient.isRoomAvailable(1L, checkIn, checkOut))
            .thenReturn(true);
        // Test logic
    }
}
```

### Integration Tests
```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ReservationIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testReservationFlow() {
        // Test full flow through API Gateway
    }
}
```

### Contract Tests
```java
@AutoConfigureStubRunner(
    ids = "com.hotel:room-service:+:stubs:8083",
    stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
class RoomServiceContractTest {
    // Verify contract compliance
}
```

## Rollback Plan

### If Issues Occur

#### 1. Immediate Rollback
```bash
# Switch DNS back to monolith
# Stop microservices
docker-compose down
```

#### 2. Data Sync
```bash
# Sync any new data back to monolith
pg_dump microservice_db | psql monolith_db
```

#### 3. Investigation
- Check logs
- Identify root cause
- Fix and retry

## Performance Optimization

### Caching Strategy
```yaml
# Add Redis caching
spring:
  cache:
    type: redis
    redis:
      time-to-live: 3600
```

### Connection Pooling
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
```

### Circuit Breaker Tuning
```yaml
resilience4j:
  circuitbreaker:
    instances:
      roomService:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
```

## Monitoring Setup

### Prometheus Configuration
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

### Grafana Dashboards
- Import Spring Boot dashboard
- Create custom service dashboards
- Setup alerts

## Security Checklist

- [ ] JWT secret configured in Config Server
- [ ] Service-to-service authentication enabled
- [ ] TLS/SSL certificates installed
- [ ] API rate limiting configured
- [ ] Database credentials encrypted
- [ ] Secrets not in version control
- [ ] CORS properly configured
- [ ] Security headers added
- [ ] Input validation on all endpoints
- [ ] SQL injection prevention

## Go-Live Checklist

- [ ] All services deployed and healthy
- [ ] Database migrations completed
- [ ] Load testing passed
- [ ] Security audit passed
- [ ] Disaster recovery plan ready
- [ ] Monitoring and alerting configured
- [ ] Documentation updated
- [ ] Team trained on new architecture
- [ ] Support escalation plan ready
- [ ] Rollback plan tested

## Post-Migration Tasks

### Week 1-2 After Go-Live
- Monitor all metrics closely
- Fix any bugs immediately
- Optimize slow queries
- Tune circuit breakers

### Week 3-4
- Performance optimization
- Cost analysis
- Team retrospective
- Document lessons learned

### Ongoing
- Regular security updates
- Performance monitoring
- Capacity planning
- Continuous improvement

## Support Contacts

- Architecture: architecture-team@hotel.com
- DevOps: devops@hotel.com
- DBA: database-team@hotel.com
- Security: security@hotel.com
- 24/7 On-call: oncall@hotel.com
