# Multi-stage build for Java Spring Boot application
FROM openjdk:17-jdk-slim as backend-build

WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY src ./src

# Install Maven
RUN apt-get update && apt-get install -y maven

# Build the application
RUN mvn clean package -DskipTests

# Frontend build stage
FROM node:18-alpine as frontend-build

WORKDIR /app/frontend

# Copy package files
COPY frontend/package*.json ./

# Install dependencies
RUN npm ci

# Copy frontend source
COPY frontend/src ./src
COPY frontend/public ./public
COPY frontend/tsconfig.json ./

# Build frontend
RUN npm run build

# Final runtime stage
FROM openjdk:17-jdk-slim

WORKDIR /app

# Install nginx for serving frontend
RUN apt-get update && apt-get install -y nginx && rm -rf /var/lib/apt/lists/*

# Copy backend jar
COPY --from=backend-build /app/target/*.jar app.jar

# Copy frontend build
COPY --from=frontend-build /app/frontend/build /var/www/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/sites-available/default

# Create logs directory
RUN mkdir -p /app/logs

# Expose ports
EXPOSE 80 8081

# Start script
COPY start.sh /start.sh
RUN chmod +x /start.sh

CMD ["/start.sh"]