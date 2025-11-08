# Hotel Management System - Architecture Documentation

## System Overview

The Hotel Management System is a cloud-native, microservices-ready application designed for scalability, reliability, and maintainability.

## Architecture Patterns

### Backend Architecture

The backend follows a **layered architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────┐
│                     REST API Layer                      │
│                   (Controllers)                         │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│                  Service Layer                          │
│              (Business Logic)                           │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│               Repository Layer                          │
│           (Data Access - JPA)                           │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│                   Database                              │
│               (PostgreSQL)                              │
└─────────────────────────────────────────────────────────┘
```

### Layers Description

#### 1. Controller Layer
- **Responsibility**: Handle HTTP requests, validate input, return responses
- **Location**: `com.hotel.management.controller`
- **Technologies**: Spring MVC, JAX-RS annotations
- **Key Classes**: 
  - `AuthController` - Authentication endpoints
  - `RoomController` - Room management
  - `ReservationController` - Booking operations

#### 2. Service Layer
- **Responsibility**: Business logic, transaction management
- **Location**: `com.hotel.management.service`
- **Technologies**: Spring Service, Transactional
- **Key Classes**:
  - `AuthServiceImpl` - User authentication
  - `RoomServiceImpl` - Room operations
  - `ReservationServiceImpl` - Booking logic

#### 3. Repository Layer
- **Responsibility**: Database access and queries
- **Location**: `com.hotel.management.repository`
- **Technologies**: Spring Data JPA
- **Key Interfaces**:
  - `UserRepository`
  - `RoomRepository`
  - `ReservationRepository`

#### 4. Entity Layer
- **Responsibility**: Domain models and database mapping
- **Location**: `com.hotel.management.entity`
- **Technologies**: JPA, Hibernate
- **Key Entities**:
  - `User`, `Guest`
  - `Room`, `RoomType`, `Amenity`
  - `Reservation`, `Payment`
  - `HousekeepingTask`, `Review`

## Security Architecture

### Authentication Flow

```
┌──────────┐         ┌──────────┐         ┌──────────┐
│  Client  │────────▶│   API    │────────▶│   JWT    │
│          │  Login  │ Gateway  │  Token  │ Provider │
└──────────┘         └──────────┘         └──────────┘
     │                                          │
     │                                          │
     └──────────── JWT Token ◄─────────────────┘
     │
     │   Subsequent Requests
     ▼
┌──────────┐         ┌──────────┐         ┌──────────┐
│  Client  │────────▶│   JWT    │────────▶│Protected │
│  +Token  │ Request │  Filter  │ Valid   │ Resource │
└──────────┘         └──────────┘         └──────────┘
```

### Security Components

1. **JwtTokenProvider**: Generate and validate JWT tokens
2. **JwtAuthenticationFilter**: Intercept requests and validate tokens
3. **SecurityConfig**: Configure security rules and permissions
4. **CustomUserDetailsService**: Load user details from database

### Role-Based Access Control (RBAC)

```
Roles:
├── GUEST
│   ├── Browse rooms
│   ├── Create reservations
│   └── Manage own bookings
│
├── FRONT_DESK
│   ├── All GUEST permissions
│   ├── Check-in/Check-out
│   └── Manage reservations
│
├── HOUSEKEEPING
│   ├── View tasks
│   ├── Update room status
│   └── Complete cleaning tasks
│
├── MANAGER
│   ├── All staff permissions
│   ├── View reports
│   ├── Manage pricing
│   └── View analytics
│
└── ADMIN
    ├── All permissions
    ├── User management
    ├── System configuration
    └── Access control
```

## Database Design

### Entity Relationships

```
User ─────< Guest ─────< Reservation >───── Room
                            │
                            │
                            ├─────< Payment
                            │
                            └─────< ReservationAddon

Room ─────< RoomType
     │
     └─────< HousekeepingTask
     │
     └─────< Review
     │
     └────▶◇ Amenity (Many-to-Many)

User ─────< HousekeepingTask (assigned_to)
```

### Key Database Patterns

1. **Soft Delete**: `deleted_at` column for logical deletion
2. **Auditing**: `created_at`, `updated_at` timestamps
3. **Optimistic Locking**: `version` field for concurrency control
4. **Indexing**: Strategic indexes on frequently queried fields

## Frontend Architecture

### Component Structure

```
src/
├── components/
│   ├── common/           # Reusable components
│   ├── guest/            # Guest-facing components
│   └── admin/            # Admin components
│
├── pages/
│   ├── guest/            # Guest pages
│   │   ├── HomePage
│   │   ├── RoomsPage
│   │   ├── BookingPage
│   │   └── LoginPage
│   │
│   └── admin/            # Admin pages
│       ├── AdminDashboard
│       ├── AdminRooms
│       └── AdminReservations
│
├── services/             # API integration
│   ├── api.ts
│   ├── authService.ts
│   └── roomService.ts
│
├── contexts/             # React Context
│   └── AuthContext.tsx
│
└── types/                # TypeScript types
    └── index.ts
```

### State Management

- **React Query**: Server state management, caching
- **React Context**: Authentication state
- **Local State**: Component-specific state with useState

## Deployment Architecture

### Kubernetes Architecture

```
┌─────────────────────────────────────────────────────┐
│                  Ingress (NGINX)                    │
│            (TLS, Load Balancing)                    │
└─────────────┬───────────────────────┬───────────────┘
              │                       │
    ┌─────────▼────────┐   ┌─────────▼────────┐
    │    Frontend      │   │     Backend      │
    │   (React App)    │   │  (Spring Boot)   │
    │   Pods: 2-5      │   │   Pods: 3-10     │
    └──────────────────┘   └─────────┬────────┘
                                     │
              ┌──────────────────────┼──────────────────┐
              │                      │                  │
    ┌─────────▼────────┐  ┌─────────▼────────┐ ┌──────▼─────┐
    │   PostgreSQL     │  │      Redis       │ │  Secrets   │
    │  (StatefulSet)   │  │   (Deployment)   │ │ (ConfigMap)│
    └──────────────────┘  └──────────────────┘ └────────────┘
```

### Scaling Strategy

#### Horizontal Pod Autoscaler (HPA)

**Backend:**
- Min: 3 pods
- Max: 10 pods
- Scale on: CPU > 70%, Memory > 80%

**Frontend:**
- Min: 2 pods
- Max: 5 pods
- Scale on: CPU > 70%

#### Database Scaling
- PostgreSQL: Vertical scaling recommended
- Read replicas for read-heavy operations
- Connection pooling (HikariCP)

#### Caching Strategy
- Redis for session management
- API response caching
- Database query caching (Hibernate L2 cache)

## API Design Principles

### RESTful Design

- **Resource-based URLs**: `/api/v1/rooms`, `/api/v1/reservations`
- **HTTP Methods**: GET (read), POST (create), PUT (update), DELETE (remove)
- **Status Codes**: Proper HTTP status codes (200, 201, 400, 404, 500)
- **Versioning**: URL versioning (`/api/v1/`)

### Error Handling

```json
{
  "status": 404,
  "message": "Room not found with id: 123",
  "timestamp": "2024-01-15T10:30:00Z",
  "errors": {
    "field": "error message"
  }
}
```

### Pagination

```
GET /api/v1/reservations?page=0&size=20&sort=createdAt,desc
```

## Performance Optimization

### Backend Optimizations

1. **Database Query Optimization**
   - Use JPA projections for read-only queries
   - Fetch only required fields
   - Batch operations where possible

2. **Caching**
   - Redis for frequently accessed data
   - Hibernate L2 cache for entities
   - HTTP caching headers

3. **Connection Pooling**
   - HikariCP with optimized settings
   - Max pool size: 10-20 connections

### Frontend Optimizations

1. **Code Splitting**
   - Lazy loading of routes
   - Dynamic imports for large components

2. **Asset Optimization**
   - Image compression
   - Minification and bundling
   - CDN for static assets

3. **API Optimization**
   - Request deduplication (React Query)
   - Caching strategies
   - Optimistic updates

## Monitoring & Observability

### Metrics

- **Application Metrics**: Spring Boot Actuator
- **System Metrics**: Kubernetes metrics
- **Custom Metrics**: Business KPIs

### Health Checks

```
/actuator/health - Overall health
/actuator/health/liveness - Liveness probe
/actuator/health/readiness - Readiness probe
```

### Logging

- Structured logging (JSON format)
- Log levels: ERROR, WARN, INFO, DEBUG
- Centralized logging with correlation IDs

## Security Best Practices

1. **Authentication**: JWT with short-lived access tokens
2. **Authorization**: Role-based access control
3. **Data Protection**: Encryption at rest and in transit
4. **Input Validation**: Bean Validation, sanitization
5. **Secrets Management**: Kubernetes Secrets, not hardcoded
6. **Network Security**: HTTPS only, CORS configured
7. **Rate Limiting**: API rate limiting (future enhancement)

## Future Enhancements

1. **Microservices Migration**
   - Split into independent services
   - Event-driven architecture with Kafka
   - Service mesh (Istio)

2. **Advanced Features**
   - Real-time notifications (WebSocket)
   - AI-powered pricing
   - Mobile applications
   - Multi-tenancy support

3. **Observability**
   - Distributed tracing (Jaeger)
   - Advanced monitoring (Prometheus + Grafana)
   - APM tools integration

---

This architecture provides a solid foundation for a scalable, maintainable, and secure hotel management system.
