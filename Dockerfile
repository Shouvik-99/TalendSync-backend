# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copy Maven files first for better caching
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (for better Docker layer caching)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose port 8080
EXPOSE 8080

# Set environment variables with defaults
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080
ENV DATABASE_URL=jdbc:postgresql://dpg-d2q36n15pdvs73djn320-a.oregon-postgres.render.com/talensyncdatabase
ENV DATABASE_USERNAME=talensyncdatabase_user
ENV DATABASE_PASSWORD=rK8PDbh38AO7dmm9omjmKpuzR0HA7Kwr
ENV JWT_SECRET_KEY=8cb9081783eb103ccf1aa01b182c6d62f0d3f249e8419d4493d7e9a74f2ddac6
ENV JWT_EXPIRATION=86400000
ENV JWT_REFRESH_EXPIRATION=604800000
ENV CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173,https://talentsync.netlify.app

# Run the application
CMD ["java", "-jar", "target/talentsync-api-0.1.0.jar"]
