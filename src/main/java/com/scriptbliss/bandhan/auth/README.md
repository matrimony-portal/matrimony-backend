# Authentication Domain Design

## Overview

The Authentication domain handles user registration, login, logout, and password reset for the Matrimony Portal. It follows Spring Boot best practices with clean architecture and separation of concerns.

## Architecture

### Package Structure
```
auth/
├── controller/          # REST API endpoints
│   ├── AuthController.java
│   ├── RegistrationController.java
│   └── ResetPasswordController.java
├── service/            # Business logic
│   ├── AuthService.java
│   ├── AuthServiceImpl.java
│   ├── RegistrationService.java
│   ├── RegistrationServiceImpl.java
│   ├── PasswordResetService.java
│   └── PasswordResetServiceImpl.java
├── repository/         # Data access layer
│   ├── UserRepository.java
│   └── VerificationTokenRepository.java
├── dto/               # Data Transfer Objects
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── CompleteRegistrationRequest.java
│   │   ├── RefreshTokenRequest.java
│   │   ├── EmailRequest.java
│   │   ├── ResetPasswordRequest.java
│   │   └── TokenValidationRequest.java
│   └── response/
│       └── AuthResponse.java
├── entity/            # JPA entities
│   ├── User.java
│   └── VerificationToken.java
└── enums/             # Enumerations
    ├── UserRole.java
    ├── TokenType.java
    ├── JwtScope.java
    └── AccountStatus.java
```

## Controllers

### 1. AuthController
**Endpoints**:
- `POST /auth/login` - User login
- `POST /auth/logout` - User logout
- `POST /auth/refresh` - Refresh JWT token

### 2. RegistrationController
**Endpoints**:
- `POST /auth/start-registration` - Start registration with email
- `POST /auth/verify-email` - Verify email and get JWT
- `POST /auth/complete-registration` - Complete profile with JWT

### 3. ResetPasswordController
**Endpoints**:
- `POST /auth/forgot-password` - Request password reset
- `POST /auth/verify-reset-token` - Verify reset token and get JWT
- `POST /auth/reset-password` - Reset password with JWT

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
    private AccountStatus status;  // Account status (ACTIVE, INACTIVE, BLOCKED)
}
```

### AccountStatus Enum
```java
public enum AccountStatus {
    ACTIVE,     // Account is active and can login
    INACTIVE,   // Account not verified or deactivated
    BLOCKED     // Account blocked by admin
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

## Services

### AuthService
- User authentication and JWT token management
- Password verification and session handling

### RegistrationService
- Email-first registration workflow
- Email verification and progressive account creation

### PasswordResetService
- Password reset request handling with security logging
- Reset token validation and password updates

## Security

### JWT Implementation
- **Centralized**: All JWT operations handled by `JwtUtil` component
- **Algorithm**: HS256 with configurable secret
- **Scopes**: ACCESS, REFRESH, REGISTRATION, PASSWORD_RESET
- **Expiration**: 24h access, 7d refresh, 30m registration, 15m reset

### Password Security
- **Hashing**: BCrypt with salt
- **Validation**: Minimum 8 characters with complexity requirements

### Security Features
- **Account Status Management**: Three states (ACTIVE, INACTIVE, BLOCKED)
- **Admin Controls**: Admins can block accounts
- **Refresh Token Rotation**: New refresh token generated on each refresh
- **Generic Error Messages**: Security-focused error responses for token operations
- **Status Validation**: Account status checked at login and token refresh

## API Documentation

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
  "message": "Login successful",
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
  },
  "timestamp": "2024-01-15T10:30:00Z"
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
  "message": "Logged out successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### POST /auth/refresh
```json
Request:
{
  "refreshToken": "refresh_token_here"
}

Response:
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "new_refresh_token_here",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "USER"
    }
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Registration Flow (Email-First)

#### POST /auth/start-registration
```json
Request:
{
  "email": "newuser@example.com"
}

Response:
{
  "success": true,
  "message": "Verification email sent. Please check your email to continue registration.",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### POST /auth/verify-email
```json
Request:
{
  "token": "verification-token-uuid"
}

Response:
{
  "success": true,
  "message": "Email verified successfully. You can now complete your registration.",
  "data": "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InVzZXJAZXhhbXBsZS5jb20iLCJzY29wZSI6IlJFR0lTVFJBVElPTiJ9...",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### POST /auth/complete-registration
```json
Request:
{
  "password": "SecurePass123!",
  "firstName": "Jane",
  "lastName": "Smith",
  "phone": "+91 9876543210",
  "role": "USER"
}

Headers:
{
  "Authorization": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InVzZXJAZXhhbXBsZS5jb20iLCJzY29wZSI6IlJFR0lTVFJBVElPTiJ9..."
}

Response:
{
  "success": true,
  "message": "Registration completed successfully. You can now login.",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Password Reset Flow

#### POST /auth/forgot-password
```json
Request:
{
  "email": "user@example.com"
}

Response:
{
  "success": true,
  "message": "Password reset email sent if account exists.",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### POST /auth/verify-reset-token
```json
Request:
{
  "token": "reset-token-uuid"
}

Response:
{
  "success": true,
  "message": "Reset token verified. You can now set your new password.",
  "data": "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InVzZXJAZXhhbXBsZS5jb20iLCJzY29wZSI6IlBBU1NXT1JEX1JFU0VUIn0...",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### POST /auth/reset-password
```json
Request:
{
  "newPassword": "NewSecurePass123!"
}

Headers:
{
  "Authorization": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InVzZXJAZXhhbXBsZS5jb20iLCJzY29wZSI6IlBBU1NXT1JEX1JFU0VUIn0..."
}

Response:
{
  "success": true,
  "message": "Password reset successfully.",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## JWT Scopes
- **ACCESS**: Full application access (login JWT)
- **REFRESH**: Token refresh capability
- **REGISTRATION**: Allows completing user registration
- **PASSWORD_RESET**: Allows resetting password

## Error Handling

### Common Error Responses
```json
{
  "success": false,
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "Invalid email or password",
    "details": null,
    "field": null
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Error Codes
- `INVALID_CREDENTIALS` - Wrong email/password
- `EMAIL_ALREADY_EXISTS` - Email already registered
- `ACCOUNT_INACTIVE` - Account not verified
- `ACCOUNT_BLOCKED` - Account blocked by admin
- `INVALID_TOKEN` - JWT token invalid/expired
- `TOKEN_EXPIRED` - Verification/reset token expired
- `AUTH_MISSING_TOKEN` - Authorization header missing
- `VALIDATION_ERROR` - Input validation failed

## Configuration

### Application Properties
```properties
# JWT Configuration
jwt.secret=${JWT_SECRET:mySecretKey123456789012345678901234567890}

# Database Configuration
spring.jpa.hibernate.ddl-auto=create-drop

# Email Configuration (AWS SES)
spring.mail.host=${SMTP_HOST:email-smtp.ap-south-1.amazonaws.com}
spring.mail.username=${SMTP_USERNAME}
spring.mail.password=${SMTP_PASSWORD}
app.email.from=${APP_EMAIL_FROM:noreply@bandhan.scriptbliss.com}
```

## Quick Start

### 1. Start Registration
```bash
curl -X POST http://localhost:8080/auth/start-registration \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com"}'
```

### 2. Complete Registration (after email verification)
```bash
curl -X POST http://localhost:8080/auth/complete-registration \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_REGISTRATION_JWT" \
  -d '{
    "password": "SecurePass123!",
    "firstName": "Test",
    "lastName": "User",
    "phone": "+91 9876543210",
    "role": "USER"
  }'
```

### 3. Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "SecurePass123!"
  }'
```