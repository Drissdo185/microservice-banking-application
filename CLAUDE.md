# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A comprehensive Spring Boot microservices-based banking application featuring user management, card services, API gateway, and service discovery. This system demonstrates modern microservices architecture with JWT authentication, inter-service communication, and containerized deployment.

## Architecture

### Technology Stack
- **Backend**: Spring Boot 3.5.3, Java 21
- **Framework**: Spring Cloud 2025.0.0
- **Database**: PostgreSQL 15 (separate databases per service)
- **Security**: Spring Security with JWT
- **Service Discovery**: Netflix Eureka
- **Gateway**: Spring Cloud Gateway
- **Build Tool**: Maven with wrapper scripts
- **Containerization**: Docker & Docker Compose

### Service Architecture
```
API Gateway (8080) → Eureka Server (8761)
    ↓
├── User Service (8081) → PostgreSQL (5435)
├── Card Service (8082) → PostgreSQL (5433)
├── Account Service (8085) → PostgreSQL (5432) [Planned]
└── Loans Service (8100) → PostgreSQL (5434) [Planned]
```

### Inter-Service Communication
- **Service Discovery**: Eureka for registration and discovery
- **Load Balancing**: Gateway-level with `lb://service-name` URIs
- **Authentication**: JWT tokens validated at gateway level
- **Client Communication**: WebClient for HTTP calls between services

## Common Commands

### Build and Run All Services
```bash
# Build all services individually
./eureka-server/mvnw -f eureka-server/pom.xml clean package
./api-gateway/mvnw -f api-gateway/pom.xml clean package  
./user-service/mvnw -f user-service/pom.xml clean package
./card-service/mvnw -f card-service/pom.xml clean package

# Start entire application with Docker Compose
docker-compose up -d

# Stop all services
docker-compose down
```

### Individual Service Development
```bash
# User Service (port 8081)
cd user-service
./mvnw spring-boot:run
./mvnw test

# Card Service (port 8082)  
cd card-service
./mvnw spring-boot:run
./mvnw test

# API Gateway (port 8080)
cd api-gateway
./mvnw spring-boot:run

# Eureka Server (port 8761)
cd eureka-server
./mvnw spring-boot:run
```

### Database Management
```bash
# Start only databases
docker-compose up -d user-db cards-db accounts-db loans-db

# Access database schemas
# User Service: ./user-service/schema.sql
# Card Service: ./card-service/schema.sql
# Account Service: ./account-service/schema.sql
```

### Health Checks and Monitoring
```bash
# Service health endpoints
curl http://localhost:8081/actuator/health  # User Service
curl http://localhost:8082/actuator/health  # Card Service
curl http://localhost:8080/actuator/health  # API Gateway

# Eureka dashboard
open http://localhost:8761
```

## Security Implementation

### JWT Authentication Flow
1. User authenticates via User Service (`POST /api/auth/login`)
2. Service returns JWT token with 24-hour expiration
3. Client includes token in `Authorization: Bearer <token>` header
4. API Gateway validates tokens using shared JWT secret
5. Valid requests routed to appropriate microservices

### Protected Routes
- All `/api/users/**` endpoints require authentication
- All `/api/cards/**` endpoints require authentication  
- All `/api/accounts/**` endpoints require authentication
- Authentication routes (`/api/auth/**`) are public

### Environment Configuration
```yaml
# JWT Configuration (shared across services)
JWT_SECRET: e9fa4786dadbdf5913ca849bc146e242f7fe646694b40ea3c88163461468ef31
JWT_EXPIRATION: 86400000  # 24 hours

# Database URLs
USER_SERVICE_DB: jdbc:postgresql://localhost:5435/user_service_db  
CARDS_SERVICE_DB: jdbc:postgresql://localhost:5433/cards_db
ACCOUNTS_SERVICE_DB: jdbc:postgresql://localhost:5432/accounts_db
LOANS_SERVICE_DB: jdbc:postgresql://localhost:5434/loans_db

# Service Discovery
EUREKA_SERVER_URL: http://localhost:8761/eureka/
```

## Service-Specific Information

### User Service (8081)
- **Purpose**: User authentication, profile management, session tracking
- **Database**: 3-table design (users, user_profiles, user_sessions)
- **Key Features**: Dual login (email/username), BCrypt password hashing, JWT session management
- **Dependencies**: Spring Security, Spring Data JPA, JWT libraries

### Card Service (8082)  
- **Purpose**: Credit/debit card management and transactions
- **Database**: 2-table design (cards, card_transactions)
- **Key Features**: Card CRUD operations, balance management, user validation via User Service
- **Dependencies**: Spring WebFlux for inter-service calls, validation framework

### API Gateway (8080)
- **Purpose**: Central entry point, routing, authentication
- **Key Features**: JWT validation filter, CORS configuration, service discovery integration
- **Routing**: Path-based routing to microservices with load balancing

### Eureka Server (8761)
- **Purpose**: Service discovery and registration
- **Key Features**: Service health monitoring, automatic service registration
- **Dashboard**: Web interface for monitoring registered services

## Development Patterns

### Package Structure (Consistent Across Services)
```
com.example.{service_name}/
├── controller/     # REST endpoints
├── service/        # Business logic layer
├── repository/     # Data access layer
├── entity/         # JPA entities
├── dto/            # Data transfer objects
├── config/         # Configuration classes
├── exception/      # Custom exceptions
├── util/           # Utility classes
└── client/         # Inter-service communication (card-service only)
```

### Error Handling
- Global exception handlers in each service (`GlobalExceptionHandler`)
- Custom business exceptions extending `RuntimeException`
- Standardized error response formats across services
- Proper HTTP status codes for different error scenarios

### Database Configuration
- **DDL Mode**: `validate` (schema must exist, no auto-creation)
- **Connection Pooling**: HikariCP (Spring Boot default)
- **Dialect**: PostgreSQL-specific optimizations
- **Migrations**: Schema files provided for each service

### Testing Strategy
- Unit tests for service layers
- Integration tests for repositories  
- Spring Boot Test annotations for proper context loading
- Separate test configurations for database connections

## Docker Configuration

### Service Dependencies
```yaml
# Startup order enforced via health checks
1. PostgreSQL databases (with health checks)
2. Eureka Server (with health check)
3. User Service (depends on user-db, eureka)
4. Card Service (depends on cards-db, user-service, eureka)
5. API Gateway (depends on eureka)
```

### Networking
- Custom bridge network (`bank-network`) for inter-service communication
- Services communicate using container names as hostnames
- Health checks ensure proper startup sequencing

## Development Guidelines

### Adding New Services
1. Follow existing package structure and naming conventions
2. Include Eureka client dependency for service discovery
3. Configure JWT authentication if endpoints need protection
4. Add health check endpoints using Spring Boot Actuator
5. Create dedicated PostgreSQL database with health checks
6. Update API Gateway routing configuration
7. Add Docker configuration with proper service dependencies

### Inter-Service Communication
- Use WebClient for HTTP calls between services
- Implement circuit breaker patterns for resilience
- Use service discovery names (`user-service`, `card-service`) as base URLs
- Handle service unavailability gracefully with proper error responses

### Security Considerations
- Never commit JWT secrets or database credentials to version control
- Use environment variables for sensitive configuration
- Implement proper input validation and sanitization
- Follow principle of least privilege for database access
- Ensure proper CORS configuration for frontend integration