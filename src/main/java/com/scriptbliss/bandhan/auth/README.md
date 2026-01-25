# Authentication Domain Design

## Overview

The Authentication domain handles user registration, login, logout, and user management for the Matrimony Portal. It follows Spring Boot best practices with clean architecture and separation of concerns.

## Architecture

### Package Structure
```
auth/
├── controller/          # REST API endpoints
│   ├── AuthController.java
│   ├── RegistrationController.java
│   └── UserController.java
├── service/            # Business logic
│   ├── AuthService.java
│   ├── RegistrationService.java
│   └── UserService.java
├── repository/         # Data access layer
│   └── UserRepository.java
├── dto/               # Data Transfer Objects
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   └── ChangePasswordRequest.java
│   └── response/
│       ├── AuthResponse.java
│       ├── UserResponse.java
│       └── ApiResponse.java
├── entity/            # JPA entities
│   └── User.java
├── enums/             # Enumerations
│   └── UserRole.java
├── mapper/            # Entity-DTO mapping
│   └── UserMapper.java
└── exception/         # Domain-specific exceptions
    ├── UserNotFoundException.java
    ├── EmailAlreadyExistsException.java
    └── InvalidCredentialsException.java
```

## Controllers

### 1. AuthController
**Purpose**: Handle authentication operations (login, logout, token refresh)

**Endpoints**:
- `POST /auth/login` - User login
- `POST /auth/logout` - User logout
- `POST /auth/refresh` - Refresh JWT token

**Responsibilities**:
- Validate login credentials
- Generate JWT tokens
- Handle logout operations
- Token refresh logic

### 2. RegistrationController
**Purpose**: Handle user registration and email verification

**Endpoints**:
- `POST /register/signup` - User registration
- `POST /register/verify-email` - Email verification
- `POST /register/resend-verification` - Resend verification email

**Responsibilities**:
- User registration validation
- Email verification workflow
- Account activation

### 3. UserController
**Purpose**: Handle user profile and account management

**Endpoints**:
- `GET /users/profile` - Get user profile
- `PUT /users/profile` - Update user profile
- `POST /users/change-password` - Change password
- `DELETE /users/account` - Delete account

**Responsibilities**:
- User profile management
- Password changes
- Account operations

## Data Models

### User Entity
```java
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    private String email;           // Unique login identifier
    private String password;        // Hashed password
    private String firstName;       // User's first name
    private String lastName;        // User's last name
    private String phone;          // Contact number
    private UserRole role;         // User role (ADMIN, EVENT_ORGANIZER, USER)
    private boolean isActive;      // Account status
}
```

### UserRole Enum
```java
public enum UserRole {
    ADMIN,              // System administrator
    EVENT_ORGANIZER,    // Event organizer
    USER               // Regular matrimony user
}
```

## DTOs (Data Transfer Objects)

### Request DTOs
- **LoginRequest**: Email, password
- **RegisterRequest**: Email, password, firstName, lastName, phone, role
- **ChangePasswordRequest**: Current password, new password

### Response DTOs
- **AuthResponse**: JWT token, user info, expires_in
- **UserResponse**: User profile data (excluding sensitive info)
- **ApiResponse**: Standard API response wrapper

## Services

### AuthService
**Responsibilities**:
- User authentication
- JWT token generation and validation
- Password verification
- Session management

### RegistrationService
**Responsibilities**:
- User registration business logic
- Email verification
- Account activation
- Duplicate email checking

### UserService
**Responsibilities**:
- User profile operations
- Password management
- Account management
- User data retrieval

## Security

### Password Security
- **Hashing**: BCrypt with salt
- **Strength**: Minimum 8 characters, mixed case, numbers, symbols
- **Storage**: Never store plain text passwords

### JWT Tokens
- **Algorithm**: HS256
- **Expiration**: 24 hours for access tokens
- **Refresh**: 7 days for refresh tokens
- **Claims**: User ID, email, role, issued time

### Role-Based Access
- **ADMIN**: Full system access
- **EVENT_ORGANIZER**: Event management + basic user features
- **USER**: Matrimony features only

## API Documentation

### Swagger UI
Access interactive API documentation at:
- **Development**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Authentication Endpoints

#### POST /auth/login
```json
Request:
{
  "email": "user@example.com",
  "password": "password123"
}

Response:
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "refresh_token_here",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "USER"
    }
  }
}
```

#### POST /auth/logout
```json
Request:
{
  "refreshToken": "refresh_token_here"
}

Response:
{
  "success": true,
  "message": "Logged out successfully"
}
```

### Registration Endpoints

#### POST /register/signup
```json
Request:
{
  "email": "newuser@example.com",
  "password": "SecurePass123!",
  "firstName": "Jane",
  "lastName": "Smith",
  "phone": "+91 9876543210",
  "role": "USER"
}

Response:
{
  "success": true,
  "message": "Registration successful. Please verify your email.",
  "data": {
    "userId": 123,
    "email": "newuser@example.com"
  }
}
```

### User Management Endpoints

#### GET /users/profile
```json
Response:
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+91 9876543210",
    "role": "USER",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z"
  }
}
```

## Error Handling

### Common Error Responses
```json
{
  "success": false,
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "Invalid email or password",
    "timestamp": "2024-01-15T10:30:00Z"
  }
}
```

### Error Codes
- `INVALID_CREDENTIALS` - Wrong email/password
- `EMAIL_ALREADY_EXISTS` - Email already registered
- `USER_NOT_FOUND` - User doesn't exist
- `ACCOUNT_INACTIVE` - Account is deactivated
- `INVALID_TOKEN` - JWT token invalid/expired
- `WEAK_PASSWORD` - Password doesn't meet requirements

## Validation Rules

### Email Validation
- Valid email format (RFC 5322)
- Maximum 255 characters
- Unique across system

### Password Validation
- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one number
- At least one special character

### Name Validation
- Maximum 100 characters each
- No special characters except hyphens and apostrophes
- Required fields

### Phone Validation
- Maximum 20 characters
- International format supported
- Optional field

## Database Schema

### users Table
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role ENUM('ADMIN', 'EVENT_ORGANIZER', 'USER') NOT NULL DEFAULT 'USER',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Indexes
- `idx_users_email` on email (unique login lookup)
- `idx_users_role` on role (role-based queries)

## Testing Strategy

### Unit Tests
- Service layer business logic
- Password hashing/verification
- JWT token generation/validation
- Input validation

### Integration Tests
- Controller endpoints
- Database operations
- Authentication flows
- Error scenarios

### Test Coverage
- Minimum 80% code coverage
- All critical paths tested
- Edge cases covered

## Configuration

### Application Properties
```properties
# JWT Configuration
jwt.secret=your-secret-key
jwt.expiration=86400
jwt.refresh-expiration=604800

# Password Configuration
password.min-length=8
password.require-uppercase=true
password.require-lowercase=true
password.require-numbers=true
password.require-special=true

# Email Configuration
email.verification.enabled=true
email.verification.expiration=3600
```

## Future Enhancements

### Planned Features
- Two-factor authentication (2FA)
- Social login (Google, Facebook)
- Password reset via email
- Account lockout after failed attempts
- Audit logging for security events

### Security Improvements
- Rate limiting for login attempts
- IP-based access controls
- Session management
- Password history tracking

---

## Quick Start

### 1. Create User
```bash
curl -X POST http://localhost:8080/register/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "SecurePass123!",
    "firstName": "Test",
    "lastName": "User",
    "phone": "+91 9876543210"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "SecurePass123!"
  }'
```

### 3. Access Protected Endpoint
```bash
curl -X GET http://localhost:8080/users/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

*This documentation follows Spring Boot best practices and should be updated as the domain evolves.*