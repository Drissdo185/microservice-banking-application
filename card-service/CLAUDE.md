# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot microservice for managing credit/debit cards in a banking application. The service handles card entities with features like credit limits, balances, and transaction tracking.

## Architecture

- **Framework**: Spring Boot 3.5.3 with Java 21
- **Database**: PostgreSQL with JPA/Hibernate
- **Build Tool**: Maven with Maven Wrapper
- **Port**: 8082
- **Package Structure**: `com.example.card_service`

### Key Components

- **Entity**: `Card` - Main entity representing cards with fields like cardNumber, cardHolderName, creditLimit, currentBalance, availableBalance
- **Repository**: `CardRepository` - JPA repository extending `JpaRepository<Card, Long>`
- **Service**: `CardService` interface and `CardServiceImpl` implementation
- **DTO**: `CardDto` for data transfer

### Database Schema

Database schema is defined in `schema.sql`:
- `cards` table: Main card information with user_id reference, card details, and balance fields
- `card_transactions` table: Transaction history linked to cards

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
./mvnw test -Dtest=CardServiceApplicationTests  # Run specific test class
```

### Database Setup
- PostgreSQL runs on localhost:5436
- Database: card_service_db
- Schema initialization: Use `schema.sql` file
- JPA DDL mode: validate (schema must exist)

## Development Notes

### Dependencies
- Spring Boot Starter Web, Data JPA, Validation
- PostgreSQL driver
- Lombok for boilerplate code reduction
- Spring Boot Test for testing

### Configuration
- Application properties in `application.yaml`
- Database connection configured for PostgreSQL
- Hibernate SQL logging disabled by default
- DDL auto set to "validate" - schema must be created manually

### Card Entity Fields
- Financial fields use `BigDecimal` with precision 15, scale 2
- Card types: CREDIT, DEBIT
- Card statuses: ACTIVE, BLOCKED, EXPIRED
- Default credit limit: 5000.00
- Timestamps with time zone support