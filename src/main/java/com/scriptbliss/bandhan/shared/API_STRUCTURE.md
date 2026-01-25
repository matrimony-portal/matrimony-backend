# API Request/Response Structure

## Overview

This document defines the standard request and response structure for all API endpoints in the Matrimony Portal. All domains must follow these conventions for consistency and maintainability.

## Standard Response Format

### Success Response
```json
{
  "success": true,
  "data": {
    // Actual response data
  },
  "message": "Optional success message",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Error Response
```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Human readable error message",
    "details": "Optional detailed error information",
    "field": "fieldName" // For validation errors
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Paginated Response
```json
{
  "success": true,
  "data": {
    "content": [
      // Array of items
    ],
    "pagination": {
      "page": 0,
      "size": 20,
      "totalElements": 150,
      "totalPages": 8,
      "first": true,
      "last": false,
      "hasNext": true,
      "hasPrevious": false
    }
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## Base Response Classes

### ApiResponse<T>
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private ErrorDetails error;
    private LocalDateTime timestamp = LocalDateTime.now();
    
    // Success constructors
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null, LocalDateTime.now());
    }
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, null, LocalDateTime.now());
    }
    
    // Error constructors
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, null, 
            new ErrorDetails(code, message, null, null), LocalDateTime.now());
    }
}
```

### ErrorDetails
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {
    private String code;
    private String message;
    private String details;
    private String field; // For validation errors
}
```

### PagedResponse<T>
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponse<T> {
    private List<T> content;
    private PaginationInfo pagination;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationInfo {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;
}
```

## Standard Error Codes

### Authentication Errors (AUTH_*)
- `AUTH_INVALID_CREDENTIALS` - Wrong email/password
- `AUTH_TOKEN_EXPIRED` - JWT token expired
- `AUTH_TOKEN_INVALID` - JWT token invalid
- `AUTH_UNAUTHORIZED` - Access denied
- `AUTH_ACCOUNT_INACTIVE` - Account deactivated
- `AUTH_EMAIL_NOT_VERIFIED` - Email not verified

### Validation Errors (VALIDATION_*)
- `VALIDATION_FAILED` - General validation error
- `VALIDATION_EMAIL_INVALID` - Invalid email format
- `VALIDATION_PASSWORD_WEAK` - Password doesn't meet requirements
- `VALIDATION_REQUIRED_FIELD` - Required field missing
- `VALIDATION_FIELD_TOO_LONG` - Field exceeds maximum length

### Business Logic Errors (BUSINESS_*)
- `BUSINESS_EMAIL_EXISTS` - Email already registered
- `BUSINESS_USER_NOT_FOUND` - User doesn't exist
- `BUSINESS_INSUFFICIENT_CREDITS` - Not enough subscription credits
- `BUSINESS_PROFILE_INCOMPLETE` - Profile not complete
- `BUSINESS_MATCH_NOT_FOUND` - Match doesn't exist

### System Errors (SYSTEM_*)
- `SYSTEM_INTERNAL_ERROR` - Internal server error
- `SYSTEM_DATABASE_ERROR` - Database connection error
- `SYSTEM_EMAIL_SERVICE_ERROR` - Email service unavailable
- `SYSTEM_RATE_LIMIT_EXCEEDED` - Too many requests

## Request Validation

### Common Validation Annotations
```java
public class BaseRequest {
    @NotBlank(message = "Field is required")
    @Size(max = 255, message = "Field must not exceed 255 characters")
    @Email(message = "Invalid email format")
    @Pattern(regexp = "regex", message = "Invalid format")
    @Min(value = 1, message = "Value must be positive")
    @Max(value = 100, message = "Value must not exceed 100")
    @Past(message = "Date must be in the past")
    @Future(message = "Date must be in the future")
}
```

### Validation Error Response
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_FAILED",
    "message": "Validation failed",
    "details": {
      "email": "Invalid email format",
      "password": "Password must contain at least one uppercase letter",
      "firstName": "First name is required"
    }
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## Domain-Specific Examples

### Authentication Domain

#### Login Request/Response
```java
// Request
@Data
public class LoginRequest {
    @NotBlank @Email
    private String email;
    
    @NotBlank
    private String password;
}

// Response
@Data
public class AuthResponse {
    private String token;
    private String refreshToken;
    private long expiresIn;
    private UserResponse user;
}
```

#### Registration Request/Response
```java
// Request
@Data
public class RegisterRequest {
    @NotBlank @Email @Size(max = 255)
    private String email;
    
    @NotBlank @Size(min = 8, max = 255)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).*$")
    private String password;
    
    @NotBlank @Size(max = 100)
    private String firstName;
    
    @NotBlank @Size(max = 100)
    private String lastName;
    
    @Size(max = 20)
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
    private String phone;
    
    private UserRole role = UserRole.USER;
}

// Response
@Data
public class RegisterResponse {
    private Long userId;
    private String email;
    private String message;
}
```

### Profile Domain

#### Profile Search Request/Response
```java
// Request
@Data
public class ProfileSearchRequest {
    @Min(18) @Max(100)
    private Integer minAge;
    
    @Min(18) @Max(100)
    private Integer maxAge;
    
    private Gender gender;
    private String religion;
    private String city;
    private String state;
    
    @Min(0) @Max(100)
    private Integer page = 0;
    
    @Min(1) @Max(50)
    private Integer size = 20;
}

// Response - Uses PagedResponse<ProfileSummary>
```

### Messaging Domain

#### Send Message Request/Response
```java
// Request
@Data
public class SendMessageRequest {
    @NotNull
    private Long receiverId;
    
    @Size(max = 255)
    private String subject;
    
    @NotBlank @Size(max = 5000)
    private String content;
}

// Response
@Data
public class MessageResponse {
    private Long messageId;
    private String status;
    private LocalDateTime sentAt;
}
```

## HTTP Status Codes

### Success Codes
- `200 OK` - Successful GET, PUT, DELETE
- `201 Created` - Successful POST (resource created)
- `204 No Content` - Successful DELETE (no response body)

### Client Error Codes
- `400 Bad Request` - Validation errors, malformed request
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Access denied (authenticated but not authorized)
- `404 Not Found` - Resource doesn't exist
- `409 Conflict` - Resource conflict (e.g., email already exists)
- `422 Unprocessable Entity` - Business logic validation failed
- `429 Too Many Requests` - Rate limit exceeded

### Server Error Codes
- `500 Internal Server Error` - Unexpected server error
- `502 Bad Gateway` - External service error
- `503 Service Unavailable` - Service temporarily unavailable

## Implementation Guidelines

### Controller Response Patterns
```java
@RestController
public class ExampleController {
    
    // Success with data
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        UserResponse user = userService.getUser(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    // Success with message
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(201)
            .body(ApiResponse.success(user, "User created successfully"));
    }
    
    // Paginated response
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<UserResponse> users = userService.getUsers(page, size);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
}
```

### Global Exception Handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(ValidationException ex) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("VALIDATION_FAILED", ex.getMessage()));
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        return ResponseEntity.unprocessableEntity()
            .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        return ResponseEntity.internalServerError()
            .body(ApiResponse.error("SYSTEM_INTERNAL_ERROR", "An unexpected error occurred"));
    }
}
```

## Testing Examples

### Controller Tests
```java
@Test
void shouldReturnSuccessResponse() {
    // Given
    UserResponse expectedUser = new UserResponse(1L, "test@example.com", "John", "Doe");
    
    // When
    ResponseEntity<ApiResponse<UserResponse>> response = controller.getUser(1L);
    
    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().isSuccess()).isTrue();
    assertThat(response.getBody().getData()).isEqualTo(expectedUser);
    assertThat(response.getBody().getTimestamp()).isNotNull();
}

@Test
void shouldReturnErrorResponse() {
    // When
    ResponseEntity<ApiResponse<Void>> response = controller.getUser(999L);
    
    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody().isSuccess()).isFalse();
    assertThat(response.getBody().getError().getCode()).isEqualTo("BUSINESS_USER_NOT_FOUND");
}
```

## Best Practices

### DO's
✅ Always use ApiResponse wrapper for consistency  
✅ Include timestamp in all responses  
✅ Use appropriate HTTP status codes  
✅ Provide meaningful error codes and messages  
✅ Validate all input data  
✅ Use pagination for list endpoints  
✅ Include field names in validation errors  

### DON'Ts
❌ Don't expose internal error details to clients  
❌ Don't use generic error messages  
❌ Don't return different response formats  
❌ Don't include sensitive data in error responses  
❌ Don't use HTTP 200 for error responses  
❌ Don't return null values (use empty arrays/objects)  

---

*This structure ensures consistent, predictable API responses across all domains in the Matrimony Portal.*