# Hotel Management System

A comprehensive, enterprise-grade hotel management system built with **Spring Boot** (Java) backend and **React** (TypeScript) frontend, designed for deployment on **Kubernetes**.

## 🏗️ Architecture

### Technology Stack

**Backend:**
- Java 17
- Spring Boot 3.2.0
- Spring Security with JWT
- Spring Data JPA / Hibernate
- PostgreSQL 16
- Redis (Caching)
- Maven
- Swagger/OpenAPI 3.0

**Frontend:**
- React 18
- TypeScript
- Material-UI (MUI)
- React Router v6
- React Query
- Axios
- Formik & Yup

**DevOps & Infrastructure:**
- Docker & Docker Compose
- Kubernetes with Kustomize
- NGINX Ingress Controller
- Horizontal Pod Autoscaler (HPA)
- PostgreSQL StatefulSet
- Redis Cache

## 🚀 Features

### Guest-Facing Features
- 🏨 Room search and booking with advanced filters
- 📅 Real-time availability checking
- 💳 Secure payment processing (Stripe integration)
- 👤 User account management
- 📱 Reservation management (view, modify, cancel)
- ⭐ Reviews and ratings
- 🎁 Promotional codes and discounts
- 📧 Email notifications

### Staff-Facing Features (Admin Panel)
- 📊 Real-time dashboard with key metrics
- 🛏️ Room management and configuration
- 📋 Reservation management system
- 🧹 Housekeeping task management
- 👥 Guest relationship management (CRM)
- 💰 Revenue management and dynamic pricing
- 📈 Reports and analytics
- 👨‍💼 Staff management
- 🏢 Front desk operations

## 📁 Project Structure

```
hotel-management-system/
├── backend/                    # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/hotel/management/
│   │   │   │   ├── config/           # Configuration classes
│   │   │   │   ├── controller/       # REST Controllers
│   │   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   ├── entity/           # JPA Entities
│   │   │   │   ├── repository/       # JPA Repositories
│   │   │   │   ├── service/          # Business Logic
│   │   │   │   ├── security/         # JWT Security
│   │   │   │   └── exception/        # Exception Handling
│   │   │   └── resources/
│   │   │       └── application.yml   # Application config
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/                   # React frontend
│   ├── public/
│   ├── src/
│   │   ├── components/         # React components
│   │   ├── pages/              # Page components
│   │   │   ├── guest/          # Guest-facing pages
│   │   │   └── admin/          # Admin panel pages
│   │   ├── services/           # API services
│   │   ├── contexts/           # React contexts
│   │   ├── hooks/              # Custom hooks
│   │   ├── types/              # TypeScript types
│   │   └── App.tsx
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
│
├── k8s/                        # Kubernetes manifests
│   ├── base/                   # Base configurations
│   └── overlays/               # Environment overlays
│       ├── dev/
│       └── prod/
│
├── docker-compose.yml          # Local development setup
└── README.md
```

## 🛠️ Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- Docker & Docker Compose
- PostgreSQL 16 (for local development)
- Maven 3.9+

### Local Development Setup

#### 1. Clone the Repository

```bash
git clone <repository-url>
cd hotel-management-system
git checkout develop
```

#### 2. Backend Setup

```bash
cd backend

# Copy environment file
cp .env.example .env

# Update .env with your configuration

# Run with Maven
./mvnw spring-boot:run

# Or build and run with Docker
docker build -t hotel-backend .
docker run -p 8080:8080 hotel-backend
```

The backend will be available at `http://localhost:8080`
API Documentation (Swagger): `http://localhost:8080/swagger-ui.html`

#### 3. Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Copy environment file
cp .env.example .env

# Start development server
npm start
```

The frontend will be available at `http://localhost:3000`

#### 4. Using Docker Compose (Recommended)

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

Services:
- Backend: http://localhost:8080
- Frontend: http://localhost:3000
- PostgreSQL: localhost:5432
- Redis: localhost:6379

## ☸️ Kubernetes Deployment

### Prerequisites

- Kubernetes cluster (v1.24+)
- kubectl configured
- Kustomize
- NGINX Ingress Controller

### Deploy to Development

```bash
# Apply development configuration
kubectl apply -k k8s/overlays/dev

# Check status
kubectl get pods -n hotel-management-dev

# Port forward (optional)
kubectl port-forward -n hotel-management-dev svc/dev-hotel-backend 8080:8080
```

### Deploy to Production

```bash
# Update secrets first!
kubectl create secret generic hotel-secrets \
  --from-literal=database-password=YOUR_PASSWORD \
  --from-literal=jwt-secret=YOUR_JWT_SECRET \
  -n hotel-management

# Apply production configuration
kubectl apply -k k8s/overlays/prod

# Monitor rollout
kubectl rollout status deployment/hotel-backend -n hotel-management
```

See [k8s/README.md](k8s/README.md) for detailed Kubernetes deployment guide.

## 📚 API Documentation

The API documentation is available via Swagger UI:

- **Local:** http://localhost:8080/swagger-ui.html
- **Production:** https://api.yourdomain.com/swagger-ui.html

### Key Endpoints

#### Authentication
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/auth/refresh` - Refresh token

#### Rooms
- `GET /api/v1/rooms` - List all rooms
- `GET /api/v1/rooms/{id}` - Get room details
- `GET /api/v1/rooms/search` - Search available rooms

#### Reservations
- `POST /api/v1/reservations` - Create reservation
- `GET /api/v1/reservations/my-reservations` - Get user's reservations
- `POST /api/v1/reservations/{id}/cancel` - Cancel reservation
- `POST /api/v1/reservations/{id}/check-in` - Check-in
- `POST /api/v1/reservations/{id}/check-out` - Check-out

## 🔒 Security

- **JWT Authentication** with access and refresh tokens
- **BCrypt** password hashing
- **CORS** configuration
- **HTTPS** enforced in production
- **Role-based access control** (GUEST, FRONT_DESK, HOUSEKEEPING, MANAGER, ADMIN)
- **Input validation** with Bean Validation
- **SQL Injection** prevention via JPA
- **XSS** protection headers

## 🧪 Testing

```bash
# Backend tests
cd backend
./mvnw test

# Frontend tests
cd frontend
npm test
```

## 📊 Database Schema

Key entities:
- **User** - System users (guests, staff, admins)
- **Guest** - Guest profiles with loyalty program
- **Room** - Hotel rooms with types and amenities
- **Reservation** - Booking information
- **Payment** - Payment transactions
- **HousekeepingTask** - Cleaning and maintenance tasks
- **Review** - Guest reviews and ratings
- **Promotion** - Discount codes and promotions

## 🔄 CI/CD Pipeline

The project is designed to integrate with CI/CD tools:

```yaml
# Example GitHub Actions workflow
name: Deploy
on:
  push:
    branches: [develop, main]
jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
      - name: Build Docker images
      - name: Push to registry
      - name: Deploy to Kubernetes
```

## 📈 Monitoring & Observability

- **Spring Boot Actuator** for health checks and metrics
- **Prometheus** metrics exposed at `/actuator/prometheus`
- **Kubernetes Liveness** and **Readiness** probes
- **Horizontal Pod Autoscaler** for auto-scaling
- **Centralized logging** (compatible with ELK stack)

## 🤝 Contributing

1. Create a feature branch from `develop`
2. Make your changes
3. Write tests
4. Submit a pull request

## 📝 License

This project is licensed under the MIT License.

## 👥 Authors

Hotel Management System Development Team

## 🆘 Support

For issues and questions:
- Create an issue in the repository
- Email: support@hotelmanagement.com

## 🗺️ Roadmap

- [ ] Mobile app (React Native)
- [ ] Multi-property support
- [ ] Advanced analytics dashboard
- [ ] Integration with OTAs (Booking.com, Expedia)
- [ ] AI-powered dynamic pricing
- [ ] Chatbot support
- [ ] Multi-language support
- [ ] Restaurant & POS integration

---

**Built with ❤️ using Spring Boot and React**
