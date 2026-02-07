#!/bin/bash

# Multi-App Docker Deployment Manager

set -e

COMPOSE_FILE="docker-compose.multi-app.yml"
ENV_FILE=".env"

echo "🚀 Multi-App Deployment Manager"

# Check Docker resources
check_resources() {
    echo "📊 Checking system resources..."
    
    # Check available memory
    AVAILABLE_MEM=$(free -m | awk 'NR==2{printf "%.0f", $7}')
    if [ "$AVAILABLE_MEM" -lt 2048 ]; then
        echo "⚠️  Warning: Low memory available (${AVAILABLE_MEM}MB). Consider upgrading."
    fi
    
    # Check disk space
    AVAILABLE_DISK=$(df -h / | awk 'NR==2{print $4}' | sed 's/G//')
    if [ "${AVAILABLE_DISK%.*}" -lt 10 ]; then
        echo "⚠️  Warning: Low disk space available (${AVAILABLE_DISK}). Consider cleanup."
    fi
    
    echo "✅ Resource check complete"
}

# Deploy all applications
deploy_all() {
    echo "📦 Deploying all applications..."
    
    check_resources
    
    if [ ! -f "$ENV_FILE" ]; then
        echo "⚠️  .env file not found. Creating from template..."
        cp .env.multi-app .env
        echo "📝 Please edit .env file with your values and run again."
        exit 1
    fi
    
    # Create shared network if it doesn't exist
    docker network create shared-network 2>/dev/null || true
    
    docker-compose -f $COMPOSE_FILE up --build -d
    
    echo "✅ All applications deployed!"
    echo "🌐 Matrimony API: http://matrimony-api.yourdomain.com/api"
    echo "🗄️  phpMyAdmin: http://phpmyadmin.yourdomain.com"
    echo ""
    echo "📝 Update your /etc/hosts file or DNS:"
    echo "127.0.0.1 matrimony-api.yourdomain.com"
    echo "127.0.0.1 phpmyadmin.yourdomain.com"
}

# Deploy specific app
deploy_app() {
    local app_name=$1
    echo "📦 Deploying $app_name..."
    
    docker-compose -f $COMPOSE_FILE up --build -d $app_name
    echo "✅ $app_name deployed!"
}

# Show status of all services
show_status() {
    echo "📋 Service Status:"
    docker-compose -f $COMPOSE_FILE ps
    
    echo ""
    echo "🔍 Resource Usage:"
    docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}"
}

# Show logs for specific service
show_logs() {
    local service=${1:-matrimony-app}
    echo "📋 Showing logs for $service..."
    docker-compose -f $COMPOSE_FILE logs -f $service
}

# Scale services
scale_service() {
    local service=$1
    local replicas=${2:-2}
    echo "⚖️  Scaling $service to $replicas replicas..."
    docker-compose -f $COMPOSE_FILE up --scale $service=$replicas -d
}

# Stop all services
stop_all() {
    echo "🛑 Stopping all services..."
    docker-compose -f $COMPOSE_FILE down
    echo "✅ All services stopped!"
}

# Cleanup resources
cleanup() {
    echo "🧹 Cleaning up resources..."
    docker-compose -f $COMPOSE_FILE down -v
    docker system prune -f
    docker volume prune -f
    echo "✅ Cleanup complete!"
}

# Backup data
backup_data() {
    echo "💾 Creating backup..."
    BACKUP_DIR="backups/$(date +%Y%m%d_%H%M%S)"
    mkdir -p $BACKUP_DIR
    
    # Backup MySQL data
    docker exec matrimony-mysql mysqldump -u bandhandb -p${MATRIMONY_DB_PASSWORD:-bandhandb@123} matrimony_portal > $BACKUP_DIR/matrimony_db.sql
    
    # Backup uploads
    docker cp matrimony-backend:/app/uploads $BACKUP_DIR/
    
    echo "✅ Backup created in $BACKUP_DIR"
}

# Monitor resources
monitor() {
    echo "📊 Monitoring resources (Press Ctrl+C to stop)..."
    while true; do
        clear
        echo "=== Multi-App Resource Monitor ==="
        date
        echo ""
        docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}\t{{.BlockIO}}"
        sleep 5
    done
}

# Main menu
case "${1:-}" in
    "deploy")
        deploy_all
        ;;
    "deploy-app")
        deploy_app $2
        ;;
    "status")
        show_status
        ;;
    "logs")
        show_logs $2
        ;;
    "scale")
        scale_service $2 $3
        ;;
    "stop")
        stop_all
        ;;
    "cleanup")
        cleanup
        ;;
    "backup")
        backup_data
        ;;
    "monitor")
        monitor
        ;;
    *)
        echo "Multi-App Docker Deployment Manager"
        echo ""
        echo "Usage: $0 {deploy|deploy-app|status|logs|scale|stop|cleanup|backup|monitor}"
        echo ""
        echo "Commands:"
        echo "  deploy              - Deploy all applications"
        echo "  deploy-app <name>   - Deploy specific application"
        echo "  status              - Show status of all services"
        echo "  logs [service]      - Show logs (default: matrimony-app)"
        echo "  scale <service> <n> - Scale service to n replicas"
        echo "  stop                - Stop all services"
        echo "  cleanup             - Clean up all resources"
        echo "  backup              - Backup application data"
        echo "  monitor             - Monitor resource usage"
        echo ""
        echo "Examples:"
        echo "  $0 deploy                    # Deploy all apps"
        echo "  $0 deploy-app matrimony-app  # Deploy only matrimony"
        echo "  $0 scale matrimony-app 3     # Scale to 3 instances"
        echo "  $0 logs nginx-proxy          # View nginx logs"
        exit 1
        ;;
esac