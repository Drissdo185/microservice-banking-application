# Microservice Banking Application

A comprehensive Spring Boot microservices-based banking application featuring user management, card services, API gateway, and service discovery. This project demonstrates modern microservices architecture patterns including JWT authentication, inter-service communication, and containerization.

## 🏗️ Architecture Overview

This application follows microservices architecture with the following components:

- **Eureka Server** - Service discovery and registration
- **API Gateway** - Central entry point with routing and authentication
- **User Service** - User authentication and profile management
- **Card Service** - Credit/debit card management
- **PostgreSQL Databases** - Separate databases per service
- **Docker Compose** - Containerized deployment

## 🚀 Services

### Core Services

| Service | Port | Database | Description |
|---------|------|----------|-------------|
| Eureka Server | 8761 | - | Service discovery and registration |
| API Gateway | 8080 | - | Request routing and authentication |
| User Service | 8081 | 5435 | User authentication and profiles |
| Card Service | 8082 | 5433 | Card management and transactions |

### Additional Services (Planned)

| Service | Port | Database | Description |
|---------|------|----------|-------------|
| Accounts Service | 8085 | 5432 | Account management |
| Loans Service | 8100 | 5434 | Loan processing |

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.5.3, Java 21
- **Framework**: Spring Cloud 2025.0.0
- **Database**: PostgreSQL 15
- **Security**: Spring Security, JWT
- **Service Discovery**: Netflix Eureka
- **Gateway**: Spring Cloud Gateway
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose
- **Additional**: Lombok, Spring Data JPA, Bean Validation

## 📦 Project Structure

```
microservice-banking-application/
├── api-gateway/                 # API Gateway service
│   ├── src/main/java/com/example/api_gateway/
│   │   ├── config/             # CORS configuration
│   │   ├── filter/             # JWT authentication filter
│   │   └── util/               # JWT utilities
│   └── Dockerfile
├── eureka-server/              # Service discovery
│   ├── src/main/java/com/example/eureka_server/
│   └── Dockerfile
├── user-service/               # User management
│   ├── src/main/java/com/example/user_service/
│   │   ├── controller/         # REST controllers
│   │   ├── service/            # Business logic
│   │   ├── entity/             # JPA entities
│   │   ├── dto/                # Data transfer objects
│   │   ├── repository/         # Data access layer
│   │   ├── config/             # Security configuration
│   │   └── util/               # Utilities
│   ├── schema.sql              # Database schema
│   
├── card-service/               # Card management
│   ├── src/main/java/com/example/card_service/
│   │   ├── controller/         # REST controllers
│   │   ├── service/            # Business logic
│   │   ├── entity/             # JPA entities
│   │   ├── dto/                # Data transfer objects
│   │   ├── repository/         # Data access layer
│   │   ├── client/             # Inter-service communication
│   │   ├── config/             # Security configuration
│   │   └── exception/          # Custom exceptions
│   ├── schema.sql              # Database schema
│   ├── API_ENDPOINTS.md        # API documentation
│   
├── docker-compose.yaml         # Container orchestration
└── springboot_microservices_todo.md  # Development roadmap
```

## 🔧 Quick Start

### Prerequisites

- Java 21
- Docker & Docker Compose
- Maven 3.6+
- PostgreSQL (if running locally)

### 1. Clone the Repository

```bash
git clone <repository-url>
cd microservice-banking-application
```

### 2. Build Services

```bash
# Build all services
./eureka-server/mvnw -f eureka-server/pom.xml clean package
./api-gateway/mvnw -f api-gateway/pom.xml clean package
./user-service/mvnw -f user-service/pom.xml clean package
./card-service/mvnw -f card-service/pom.xml clean package
```

### 3. Start with Docker Compose

```bash
docker-compose up -d
```

### 4. Verify Services

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **User Service**: http://localhost:8081/actuator/health
- **Card Service**: http://localhost:8082/actuator/health

## 🔐 Security & Authentication

### JWT Authentication Flow

1. User registers/logs in via User Service
2. Service returns JWT token
3. Client includes token in Authorization header
4. API Gateway validates token for protected endpoints
5. Valid requests are routed to appropriate services

### Security Features

- **Password Encryption**: BCrypt hashing
- **JWT Tokens**: Stateless authentication
- **Session Management**: Database-tracked sessions
- **CORS Configuration**: Cross-origin request handling
- **Method-level Security**: Protected endpoints

## 📡 API Endpoints

### User Service (`/api/auth/*`, `/api/users/*`)

```bash
# Authentication
POST /api/auth/register    # User registration
POST /api/auth/login       # User login
POST /api/auth/logout      # User logout
GET  /api/auth/validate    # Token validation

# User Management
GET  /api/users/profile    # Get user profile
PUT  /api/users/profile    # Update user profile
```

### Card Service (`/api/cards/*`)

```bash
# Card Management
GET    /api/cards              # Get user's cards
POST   /api/cards              # Create new card
GET    /api/cards/{id}         # Get card details
PUT    /api/cards/{id}         # Update card
DELETE /api/cards/{id}         # Delete card
PUT    /api/cards/{id}/status  # Update card status
```

## 🗄️ Database Schema

### User Service Database

- **users**: Core user authentication data
- **user_profiles**: Extended user information
- **user_sessions**: JWT session tracking

### Card Service Database

- **cards**: Card information and balances
- **card_transactions**: Transaction history

## 🔄 Inter-Service Communication

- **Service Discovery**: Eureka for service registration
- **Client Communication**: WebClient for HTTP calls
- **Circuit Breaker**: Resilience patterns (planned)
- **Load Balancing**: Gateway-level distribution

## 🧪 Testing

### Running Tests

```bash
# User Service
cd user-service
./mvnw test

# Card Service
cd card-service
./mvnw test
```

### Test Coverage

- Unit tests for service layers
- Integration tests for repositories
- API endpoint testing
- Security configuration testing

## 📊 Monitoring & Health Checks

- **Health Endpoints**: Spring Boot Actuator
- **Service Status**: Eureka dashboard
- **Database Health**: Connection monitoring
- **Container Health**: Docker health checks

## 🚧 Development Status

### ✅ Completed Features

- [x] Eureka Server setup
- [x] API Gateway with JWT authentication
- [x] User Service with authentication
- [x] Card Service with CRUD operations
- [x] Docker containerization
- [x] Database schemas and migrations
- [x] Security configuration
- [x] Inter-service communication

### 🔄 In Progress

- [ ] Transaction Service
- [ ] Analytics Service
- [ ] Enhanced error handling
- [ ] Comprehensive testing

### 📋 Planned Features

- [ ] Loan Service
- [ ] Account Service
- [ ] Notification Service
- [ ] Monitoring dashboard
- [ ] CI/CD pipeline

## 🐳 Docker Configuration

### Services in Docker Compose

- **Application Services**: All microservices with health checks
- **Databases**: Separate PostgreSQL instances per service
- **Networking**: Custom bridge network for inter-service communication
- **Volumes**: Persistent data storage for databases

### Container Health Checks

All services include health checks for:
- Database connectivity
- Service startup verification
- Dependency readiness

## 🔧 Configuration

### Environment Variables

```yaml
# JWT Configuration
JWT_SECRET: your-secret-key
JWT_EXPIRATION: 86400000

# Database Configuration
SPRING_DATASOURCE_URL: jdbc:postgresql://host:port/database
SPRING_DATASOURCE_USERNAME: username
SPRING_DATASOURCE_PASSWORD: password

# Service Discovery
EUREKA_SERVER_URL: http://eureka-server:8761/eureka/
```

### Application Profiles

- **Development**: Local database connections
- **Docker**: Container-based configuration
- **Production**: External configuration (planned)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request
