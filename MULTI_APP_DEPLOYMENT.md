# Multi-App Cloud Deployment Guide

## Overview
This setup allows you to deploy multiple applications on a single machine using Docker with:
- Nginx reverse proxy for routing
- Shared resources optimization
- Easy scaling and management
- Resource monitoring

## Quick Start

### 1. Initial Setup
```bash
# Make deployment script executable
chmod +x multi-app-deploy.sh

# Copy environment template
cp .env.multi-app .env

# Edit environment variables
nano .env
```

### 2. Deploy All Applications
```bash
./multi-app-deploy.sh deploy
```

### 3. Configure DNS/Hosts
Add to `/etc/hosts` or configure DNS:
```
YOUR_SERVER_IP matrimony-api.yourdomain.com
YOUR_SERVER_IP phpmyadmin.yourdomain.com
```

## Cloud Platform Deployment

### AWS EC2
```bash
# Launch EC2 instance (t3.medium recommended)
# Install Docker and Docker Compose
sudo yum update -y
sudo yum install -y docker
sudo systemctl start docker
sudo usermod -a -G docker ec2-user

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Deploy
./multi-app-deploy.sh deploy
```

### Google Cloud Platform
```bash
# Create VM instance
gcloud compute instances create matrimony-server \
    --image-family=ubuntu-2004-lts \
    --image-project=ubuntu-os-cloud \
    --machine-type=e2-medium \
    --boot-disk-size=50GB

# SSH and deploy
gcloud compute ssh matrimony-server
# Follow AWS EC2 steps above
```

### DigitalOcean
```bash
# Create droplet with Docker pre-installed
# Use Docker 1-Click App or install manually
./multi-app-deploy.sh deploy
```

## Adding New Applications

### 1. Update docker-compose.multi-app.yml
```yaml
  new-app:
    image: your-new-app:latest
    container_name: new-app
    expose:
      - "3000"
    networks:
      - shared-network
    restart: unless-stopped
```

### 2. Add Nginx Configuration
Create `nginx/conf.d/new-app.conf`:
```nginx
server {
    listen 80;
    server_name new-app.yourdomain.com;

    location / {
        proxy_pass http://new-app:3000/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## Resource Management

### Monitor Resources
```bash
./multi-app-deploy.sh monitor
```

### Scale Services
```bash
./multi-app-deploy.sh scale matrimony-app 3
```

### View Logs
```bash
./multi-app-deploy.sh logs matrimony-app
```

## SSL/HTTPS Setup

### Using Let's Encrypt
```bash
# Install certbot
sudo apt install certbot python3-certbot-nginx

# Get certificates
sudo certbot --nginx -d matrimony-api.yourdomain.com -d phpmyadmin.yourdomain.com

# Auto-renewal
sudo crontab -e
# Add: 0 12 * * * /usr/bin/certbot renew --quiet
```

## Backup and Recovery

### Create Backup
```bash
./multi-app-deploy.sh backup
```

### Restore from Backup
```bash
# Restore database
docker exec -i matrimony-mysql mysql -u bandhandb -p matrimony_portal < backup_file.sql

# Restore uploads
docker cp backup_uploads/ matrimony-backend:/app/uploads/
```

## Production Optimizations

### 1. Resource Limits
Already configured in docker-compose.multi-app.yml

### 2. Log Rotation
```bash
# Add to /etc/docker/daemon.json
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "10m",
    "max-file": "3"
  }
}
```

### 3. Health Checks
Health checks are configured for critical services

### 4. Auto-restart
All services configured with `restart: unless-stopped`

## Troubleshooting

### Check Service Status
```bash
./multi-app-deploy.sh status
```

### View Service Logs
```bash
./multi-app-deploy.sh logs [service-name]
```

### Restart Specific Service
```bash
docker-compose -f docker-compose.multi-app.yml restart [service-name]
```

### Clean Up Resources
```bash
./multi-app-deploy.sh cleanup
```

## Security Considerations

1. **Firewall**: Only expose ports 80, 443, and SSH
2. **Environment Variables**: Keep sensitive data in .env file
3. **Regular Updates**: Update Docker images regularly
4. **Monitoring**: Set up log monitoring and alerts
5. **Backups**: Schedule regular automated backups

## Cost Optimization

- **Resource Limits**: Prevent resource overconsumption
- **Shared Services**: Use shared MySQL, Redis, etc.
- **Auto-scaling**: Scale down during low usage
- **Monitoring**: Track resource usage patterns