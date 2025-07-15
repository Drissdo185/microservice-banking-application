# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot microservice for managing loans in a banking application. The service handles loan creation, payment processing, EMI calculations, and loan lifecycle management with features like status tracking and user validation.

## Architecture

- **Framework**: Spring Boot 3.5.3 with Java 21
- **Database**: PostgreSQL with JPA/Hibernate
- **Build Tool**: Maven with Maven Wrapper
- **Port**: 8100
- **Package Structure**: `com.example.loan_service`

### Key Components

- **Entity**: `Loan` and `LoanPayment` - Core entities representing loans and payment history
- **Repository**: `LoanRepository` and `LoanPaymentRepository` - JPA repositories with custom queries
- **Service**: `LoanService` interface and `LoanServiceImpl` implementation with comprehensive business logic
- **DTO**: `LoanDto`, `LoanPaymentDto`, and `UserValidationResponse` for data transfer
- **Client**: `UserServiceClient` for inter-service communication and user validation

### Database Schema

Database schema is defined in `schema.sql`:
- `loans` table: Main loan information with user_id reference, financial details, and status tracking
- `loan_payments` table: Payment history linked to loans with foreign key relationship

## Common Commands

### Build and Run
```bash
./mvnw clean compile                 # Compile the application
./mvnw clean package                 # Build JAR file
./mvnw spring-boot:run              # Run the application locally
```

### Testing
```bash
./mvnw test                         # Run all tests
./mvnw test -Dtest=LoanServiceApplicationTests  # Run specific test class
```

### Database Setup
- PostgreSQL runs on localhost:5434
- Database: loan_service_db
- Schema initialization: Use `schema.sql` file
- JPA DDL mode: validate (schema must exist)

## Development Notes

### Dependencies
- Spring Boot Starter Web, Data JPA, Security, Validation
- Spring Cloud Netflix Eureka Client for service discovery
- Spring Boot Starter WebFlux for inter-service communication
- Spring Boot Starter Actuator for monitoring
- PostgreSQL driver
- Lombok for boilerplate code reduction
- JWT libraries for token handling

### Configuration
- Application properties in `application.yaml`
- Database connection configured for PostgreSQL
- JWT secret and expiration settings
- Eureka client configuration for service registration
- Actuator endpoints enabled for monitoring

### Loan Entity Fields
- Financial fields use `BigDecimal` with precision 10, scale 2
- Loan statuses: ACTIVE, PAID, DEFAULT
- EMI calculation uses compound interest formula
- Timestamps with automatic creation and update tracking

### Inter-Service Communication
- Uses WebClient with load balancing for calling User Service
- Service discovery through Eureka for service-to-service calls
- Circuit breaker pattern implemented for resilience
- JWT token validation for secure communication

## Business Logic

### EMI Calculation
The service implements a sophisticated EMI calculation using the standard compound interest formula:
```
EMI = P × r × (1 + r)^n / ((1 + r)^n - 1)
```
Where:
- P = Principal loan amount
- r = Monthly interest rate (annual rate / 12 / 100)
- n = Number of months (tenure)

### Loan Management Rules
1. **Creation Limits**: Maximum 3 active loans per user
2. **Payment Processing**: Automatic outstanding balance updates and status changes
3. **Status Management**: Automatic status change to "PAID" when balance reaches zero
4. **Validation**: User existence validation via User Service before loan creation

### Security Features
- JWT-based authentication for all protected endpoints
- User ID extraction from JWT tokens for authorization
- Inter-service authentication using bearer tokens
- Request filtering and validation

## API Design

### REST Endpoints
- Full CRUD operations for loan management
- Payment processing with automatic calculations
- Status management and reporting
- EMI calculation utility endpoints
- Health check endpoints for monitoring

### Error Handling
- Global exception handler with standardized error responses
- Custom business exceptions for specific scenarios
- Proper HTTP status codes for different error types
- Comprehensive validation with detailed error messages

## Testing Strategy
- Unit tests for service layers and business logic
- Integration tests for repositories and database operations
- Test database configuration separate from main application
- Use Spring Boot Test annotations for proper test context

## Docker Configuration
- Dockerfile configured for Java 21 with slim base image
- Application runs on port 8100 within containers
- Database connection configured for containerized PostgreSQL
- Health checks implemented for proper startup sequencing

## Development Guidelines

### Code Style
- Follow existing package structure and naming conventions
- Use Lombok annotations to reduce boilerplate code
- Implement proper validation and error handling
- Follow REST API design principles

### Database Operations
- Use BigDecimal for all financial calculations
- Implement proper transaction management
- Use JPA queries for complex operations
- Maintain referential integrity with foreign keys

### Inter-Service Communication
- Always validate users before creating loans
- Implement proper error handling for service calls
- Use service discovery names for service-to-service calls
- Handle service unavailability gracefully

### Security Considerations
- Validate JWT tokens on all protected endpoints
- Extract user context from tokens for authorization
- Implement proper input validation and sanitization
- Follow principle of least privilege for database access