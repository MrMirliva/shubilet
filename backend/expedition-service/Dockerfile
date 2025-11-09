# -------- Stage 1: Build --------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy project
COPY . .

# Build (skip tests for faster image builds)
RUN [ -f "./mvnw" ] && ./mvnw -q clean package -DskipTests || mvn -q clean package -DskipTests

# -------- Stage 2: Run --------
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy fat JAR
COPY --from=build /app/target/*.jar app.jar

# Service default port (align with application.properties)
EXPOSE 8081

# Common envs; override via docker-compose.yml
# Eureka client will discover through the server container name
ENV EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
ENV SPRING_PROFILES_ACTIVE=docker

# Database connection (override in compose for safety)
# Example for PostgreSQL running in a "postgres" service:
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/expedition_db
ENV SPRING_DATASOURCE_USERNAME=expedition_user
ENV SPRING_DATASOURCE_PASSWORD=changeme
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=validate

ENTRYPOINT ["java", "-jar", "app.jar"]
