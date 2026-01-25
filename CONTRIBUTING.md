# Contributing to Matrimony Backend

## 🚀 Quick Setup

### Prerequisites
- Java 17+
- Docker & Docker Compose
- IDE: STS/Eclipse/IntelliJ/VS Code

### Clone & Start
```bash
git clone <repo-url>
cd matrimony-backend

# Start database
docker compose up -d

# Set environment variables (see STS_ENV_SETUP.md)
# Then start application
./mvnw spring-boot:run
```

### Verify Setup
```bash
# Check API
curl http://localhost:8080/swagger-ui.html

# Database access
open http://localhost:3307  # phpMyAdmin
# Login: bandhandb / bandhandb@123
```

## 📁 Project Structure

```
src/main/java/com/scriptbliss/bandhan/
├── auth/           # 🔐 Authentication & Registration
├── profile/        # 👤 User Profiles
├── match/          # 💕 Matching Algorithm
├── interest/       # ❤️ User Interests
├── chat/           # 💬 Messaging
├── payment/        # 💳 Payments
├── admin/          # ⚙️ Admin Panel
└── shared/         # 🔧 Common Components
```

### Domain Architecture
```
auth/
├── controller/     # REST endpoints
├── service/        # Business logic
├── repository/     # Data access
├── entity/         # JPA entities
├── dto/request/    # Request DTOs
├── dto/response/   # Response DTOs
└── enums/          # Domain enums
```

## 🛠️ Development Guidelines

### API Standards
- Use `ApiResponse<T>` wrapper for all responses
- Validate requests with `@Valid`
- Handle errors with `GlobalExceptionHandler`
- Document APIs with Swagger annotations

### Code Style
- Use Lombok annotations (`@Getter`, `@Setter`, `@Builder`)
- Follow Spring Boot conventions
- Write meaningful JavaDoc for public methods
- Use `@Transactional` for service methods

### Security
- Never expose internal IDs in public APIs
- Use silent fail for user enumeration prevention
- Validate and sanitize all inputs
- Log security events appropriately

## 🧪 Testing

### Current Endpoints
```bash
# Registration
POST /register/signup
POST /register/verify-email?token=xyz
POST /register/resend-verification?email=user@example.com

# Documentation
GET /swagger-ui.html
```

### Database
- **MySQL**: localhost:3306
- **phpMyAdmin**: http://localhost:3307
- **Credentials**: bandhandb / bandhandb@123

## 🔧 Environment Setup

### IDE Configuration
- See `STS_ENV_SETUP.md` for STS/Eclipse setup
- Set environment variables: `DB_PASSWORD`, `SMTP_USERNAME`, `SMTP_PASSWORD`
- Import as Maven project

### Database Schema
- See `DatabaseDesign/schema.sql` for complete schema
- See `DatabaseDesign/database_design.md` for documentation

## 📝 Contributing Rules

1. **Follow domain structure** - Keep related code together
2. **Use DTOs** - Never expose entities directly
3. **Handle errors properly** - Use business exceptions
4. **Write tests** - Cover business logic
5. **Document APIs** - Use Swagger annotations
6. **Security first** - Follow security best practices

## 🔒 Current Security Status

- **Authentication**: Disabled (development mode)
- **Authorization**: All endpoints public
- **CSRF**: Disabled
- **Email verification**: Implemented
- **Password hashing**: BCrypt enabled

## 🆘 Need Help?

- Check existing patterns in `auth` domain
- Review `shared` components for utilities
- Follow established naming conventions
- Ask team for architecture decisions
