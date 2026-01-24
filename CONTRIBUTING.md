# Contributing to Matrimony Backend

## 🚀 Quick Setup

### 1. Prerequisites
```bash
# Check versions
java -version      # Should be 17+
./mvnw -version    # Should be 3.6+
docker --version   # Any recent version

# Install required tools
- Java 17+
- Maven Wrapper (included)
- Docker & Docker Compose
- Spring Boot 4.0.2
```

### 2. Clone & Start
```bash
git clone <repo-url>
cd matrimony-backend

# Start database
docker compose up -d

# Start application (use wrapper if mvn not installed)
./mvnw spring-boot:run
# OR if Maven installed:
# mvn spring-boot:run
```

### 3. Verify Setup
```bash
# Test API
curl http://localhost:8080/api/api/test/health
# Expected: "OK"

# Access phpMyAdmin
open http://localhost:3307
# Login: bandhandb / bandhandb@123
```

## 🐳 Docker Services

### Database Stack
```bash
# Start services
docker compose up -d

# Check status
docker compose ps

# View logs
docker compose logs bandhan-mysql

# Stop services
docker compose down
```

### Services Running
- **MySQL**: localhost:3306 (bandhan-mysql container)
- **phpMyAdmin**: http://localhost:3307 (bandhan-phpmyadmin container)
- **Spring Boot 4.0.2**: http://localhost:8080/api

## 📁 Project Structure

```
src/main/java/com/scriptbliss/bandhan/
├── auth/           # 🔐 Authentication
├── profile/        # 👤 User profiles
├── match/          # 💕 Matching
├── interest/       # ❤️ Interests
├── chat/           # 💬 Messaging
├── payment/        # 💳 Payments
├── admin/          # ⚙️ Admin
└── shared/         # 🔧 Common
```

### Domain Structure
```
profile/
├── controller/     # REST endpoints
├── service/        # Business logic
├── repository/     # Data access
├── entity/         # JPA entities
├── dto/            # Request/Response
└── mapper/         # Entity-DTO mapping
```

## 🛠️ Development

### Adding New Features
1. Choose domain folder
2. Follow structure above
3. Use DTOs for API
4. Write tests

### Code Example
```java
@RestController
@RequestMapping("/profile")
public class ProfileController {
    @GetMapping("/{id}")
    public ProfileResponse getProfile(@PathVariable Long id) {
        return profileService.getProfile(id);
    }
}
```

### Auto-Reload
- DevTools enabled - code changes auto-reload
- No server restart needed

## 🧪 Testing

### API Endpoints
```bash
# All endpoints prefixed with /api
curl http://localhost:8080/api/api/test/health
curl http://localhost:8080/api/actuator/health
```

### Run Tests
```bash
./mvnw test
# OR if Maven installed: mvn test
```

### Database Access
- **phpMyAdmin**: http://localhost:3307
- **Login**: bandhandb / bandhandb@123

## 🔒 Security
- All endpoints are public (development mode)
- Authentication disabled
- Will be enabled later

## 🆘 Help
- Check existing code patterns
- Follow domain structure
- Ask team for guidance