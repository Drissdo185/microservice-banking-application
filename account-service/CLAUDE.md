# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot microservice for managing bank accounts in a banking application. The service handles account entities with features like account creation, transaction management, and balance tracking.

## Architecture

- **Framework**: Spring Boot 3.5.3 with Java 21
- **Database**: PostgreSQL with JPA/Hibernate
- **Build Tool**: Maven with Maven Wrapper
- **Port**: 8085
- **Package Structure**: `com.example.account_service`

### Key Components

- **Entity**: `Account` - Main entity representing bank accounts with fields like accountNumber, accountType, balance, status
- **Entity**: `AccountTransaction` - Transaction history entity linked to accounts
- **Repository**: `AccountRepository` and `AccountTransactionRepository` - JPA repositories
- **Service**: `AccountService` interface and `AccountServiceImpl` implementation
- **Controller**: `AccountController` - REST endpoints
- **DTOs**: `AccountDto`, `TransactionDto`, `CreateAccountRequest`, `TransactionRequest`

### Database Schema

Database schema is defined in `schema.sql`:
- `accounts` table: Main account information with user_id reference, account details, and balance fields
- `account_transactions` table: Transaction history linked to accounts

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
./mvnw test -Dtest=AccountServiceApplicationTests  # Run specific test class
```

### Database Setup
- PostgreSQL runs on localhost:5432
- Database: accounts_db
- Schema initialization: Use `schema.sql` file
- JPA DDL mode: validate (schema must exist)

## Development Notes

### Dependencies
- Spring Boot Starter Web, Data JPA, Security, Validation, WebFlux
- Spring Cloud Eureka Client for service discovery
- PostgreSQL driver
- JWT libraries for token handling
- Lombok for boilerplate code reduction

### Configuration
- Application properties in `application.properties`
- Database connection configured for PostgreSQL
- JWT configuration for authentication
- Eureka client configuration for service discovery
- User service integration via WebClient

### Security Features
- JWT-based authentication via User Service validation
- All endpoints require authentication except actuator
- User ownership validation for all account operations
- Input validation on all request DTOs

### Business Rules
- Users can have maximum 1 savings account
- Users can have maximum 3 checking accounts
- Accounts cannot be closed with non-zero balance
- Debit transactions require sufficient balance
- Account numbers are auto-generated (12 digits)

### API Endpoints
- `POST /api/accounts` - Create new account
- `GET /api/accounts` - Get user's accounts
- `GET /api/accounts/{id}` - Get specific account
- `GET /api/accounts/number/{number}` - Get account by number
- `PUT /api/accounts/{id}/close` - Close account
- `POST /api/accounts/{id}/transactions` - Add transaction
- `GET /api/accounts/{id}/transactions` - Get account transactions (paginated)
- `GET /api/accounts/{id}/transactions/history` - Get all account transactions
- `GET /api/accounts/transactions` - Get all user transactions (paginated)

### Inter-Service Communication
- Uses WebClient to validate users via User Service
- LoadBalanced WebClient for service discovery integration
- Circuit breaker patterns for resilience (can be added)

### Account Types and Limits
- `CHECKING`: Up to 3 accounts per user
- `SAVINGS`: Up to 1 account per user

### Transaction Types
- `CREDIT`: Adds money to account
- `DEBIT`: Removes money from account (requires sufficient balance)

### Error Handling
- Custom exceptions: `AccountNotFoundException`, `InsufficientBalanceException`, `AccountAlreadyExistsException`
- Global exception handler with proper HTTP status codes
- Input validation with detailed error messages