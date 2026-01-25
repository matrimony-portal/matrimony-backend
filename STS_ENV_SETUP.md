# STS Environment Setup Guide

## Setting Environment Variables for Matrimony Backend

### Run Configuration Setup

1. **Open Run Configuration**
   - Right-click `matrimony-backend` project → **Run As** → **Run Configurations**
   - Select **Spring Boot App** → `MatrimonyBackendSpringApplication`

2. **Add Environment Variables**
   - Go to **Environment** tab
   - Click **New** button
   - Add each variable:

   | Name | Value |
   |------|-------|
   | `DB_PASSWORD` | `your_database_password` |
   | `SMTP_USERNAME` | `your_aws_ses_smtp_username` |
   | `SMTP_PASSWORD` | `your_aws_ses_smtp_password` |

3. **Apply and Run**
   - Click **Apply** → **Run**
   - Application should start without database connection errors

## Verification

1. **Check Console Output**
   - Look for successful database connection
   - No "Could not resolve placeholder" errors
   - Server starts on port 8080

2. **Test Application**
   - Visit: `http://localhost:8080/swagger-ui.html`
   - Should load Swagger documentation

## Troubleshooting

- **Database connection fails**: Ensure MySQL is running on port 3306
- **Variables not loading**: Double-check variable names match exactly
- **Still getting errors**: Restart STS and try again