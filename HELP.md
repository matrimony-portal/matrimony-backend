# Matrimony Backend - Quick Help

## 🚀 Getting Started

### Environment Setup
1. **Set environment variables** (see `STS_ENV_SETUP.md`)
   - `DB_PASSWORD=bandhandb@123`
   - `SMTP_USERNAME=your_aws_ses_username`
   - `SMTP_PASSWORD=your_aws_ses_password`

2. **Start database**
   ```bash
   docker compose up -d
   ```

3. **Run application**
   ```bash
   ./mvnw spring-boot:run
   ```

## 📋 Quick Reference

### API Endpoints
- **Registration**: `POST /register/signup`
- **Email Verification**: `POST /register/verify-email?token=xyz`
- **Resend Verification**: `POST /register/resend-verification?email=user@example.com`
- **API Documentation**: `GET /swagger-ui.html`

### Database Access
- **phpMyAdmin**: http://localhost:3307
- **Credentials**: bandhandb / bandhandb@123

### Project Structure
```
src/main/java/com/scriptbliss/bandhan/
├── auth/           # Authentication & Registration
├── shared/         # Common utilities
└── [other domains] # Future features
```

## 🔧 Common Issues

### Database Connection Failed
- Check if Docker containers are running: `docker compose ps`
- Verify environment variables are set
- Restart containers: `docker compose restart`

### Email Not Sending
- Verify AWS SES credentials in environment variables
- Check application logs for email service errors
- Ensure sender email is verified in AWS SES

### Build Errors
- Clean and rebuild: `./mvnw clean compile`
- Check Java version: `java -version` (should be 17+)
- Verify Maven wrapper: `./mvnw -version`

## 📚 Documentation

- **Contributing**: See `CONTRIBUTING.md`
- **Database Design**: See `DatabaseDesign/database_design.md`
- **STS Setup**: See `STS_ENV_SETUP.md`
- **API Docs**: http://localhost:8080/swagger-ui.html (when running)

## 🆘 Need Help?

1. Check existing documentation files
2. Review error logs in console
3. Verify environment setup
4. Ask team for assistance

