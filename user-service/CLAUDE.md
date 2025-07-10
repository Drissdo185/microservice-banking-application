# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Build and Run
- `./mvnw clean install` - Build the project
- `./mvnw spring-boot:run` - Run the application (starts on port 8081)
- `./mvnw test` - Run all tests
- `./mvnw test -Dtest=ClassName` - Run specific test class

### Database
- PostgreSQL database required (configured for localhost:5435)
- Database name: `user_service_db`
- Schema is provided in `schema.sql`
- Application uses Liquibase for database migrations

## Architecture Overview

### Service Layer Structure
This is a Spring Boot microservice with JWT-based authentication following clean architecture principles:

**AuthService** - Handles user registration, login, logout, and token management
- Supports dual login (email or username)
- JWT token generation and validation
- Session management with database storage
- Password encryption with BCrypt

**UserService** - Manages user profiles and account operations
- Profile retrieval and updates
- User lookup by ID, username, or email
- Account deletion with proper session cleanup

### Key Components
- **Security**: JWT-based authentication with Spring Security
- **Database**: PostgreSQL with JPA/Hibernate (3-table design: users, user_profiles, user_sessions)
- **Validation**: Bean validation with custom exceptions
- **API**: RESTful endpoints for auth (`/api/auth/*`) and user management (`/api/users/*`)

### Entity Relationships
- `User` entity contains core authentication data (username, email, password)
- `UserProfile` entity contains profile information (first_name, last_name, phone)
- `UserSession` entity tracks JWT tokens and expiration times

## Configuration

### Application Properties
- Server runs on port 8081
- JWT secret and expiration configurable via `application.yaml`
- Database connection to PostgreSQL
- Actuator endpoints enabled for monitoring
- Eureka client configuration for service discovery

### Environment Setup
- Java 21 required
- PostgreSQL database must be running on localhost:5435
- JWT secret is configured in application.yaml (should be externalized for production)

## Testing Approach
- Unit tests for services and utilities
- Integration tests for controllers
- Test database configuration separate from main application
- Use Spring Boot Test annotations for proper test context

## Key Security Features
- JWT token-based authentication
- Password hashing with BCrypt
- Token blacklisting through session management
- Protected endpoints with proper authorization
- CORS configuration for frontend integration

## API Endpoints
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `GET /api/auth/validate` - Token validation
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile