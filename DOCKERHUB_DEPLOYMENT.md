# DockerHub Deployment Guide for TalentSync API

## Prerequisites
- Docker installed on your machine
- DockerHub account (free at hub.docker.com)

## Step 1: Build Your Spring Boot Application
```bash
cd /Users/s0d0iet/Desktop/TalentSync/backend-java

# Clean and build the application
mvn clean package -DskipTests
```

## Step 2: Build Docker Image
```bash
# Build the Docker image
docker build -t talentsync-api:latest .

# Tag for DockerHub (replace 'yourusername' with your DockerHub username)
docker tag talentsync-api:latest yourusername/talentsync-api:latest
docker tag talentsync-api:latest yourusername/talentsync-api:v1.0
```

## Step 3: Login to DockerHub
```bash
# Login to DockerHub
docker login

# Enter your DockerHub username and password
```

## Step 4: Push to DockerHub
```bash
# Push the image to DockerHub
docker push yourusername/talentsync-api:latest
docker push yourusername/talentsync-api:v1.0
```

## Step 5: Test Locally (Optional)
```bash
# Test the image locally
docker run -p 8080:8080 \
  -e OPENAI_API_KEY=your-openai-key \
  yourusername/talentsync-api:latest

# Or use docker-compose
docker-compose up
```

## Step 6: Deploy Anywhere
Your image is now available on DockerHub and can be deployed on:
- ✅ **Render** (supports Docker containers)
- ✅ **DigitalOcean App Platform** 
- ✅ **Google Cloud Run**
- ✅ **AWS ECS/Fargate**
- ✅ **Azure Container Instances**
- ✅ **Any VPS with Docker**

## Free Deployment Options with Docker:
1. **Render** - Free tier supports Docker containers
2. **Google Cloud Run** - 2M requests/month free
3. **Oracle Cloud** - Always free compute instances
4. **Azure Container Instances** - Free tier available

## Environment Variables for Deployment:
```
DATABASE_URL=jdbc:postgresql://your-db-host:5432/database
DATABASE_USERNAME=your-db-user
DATABASE_PASSWORD=your-db-password
JWT_SECRET_KEY=8cb9081783eb103ccf1aa01b182c6d62f0d3f249e8419d4493d7e9a74f2ddac6
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
OPENAI_API_KEY=your-openai-api-key
CORS_ALLOWED_ORIGINS=https://talentsync.netlify.app
```

## Your DockerHub Image URL:
`docker pull yourusername/talentsync-api:latest`
