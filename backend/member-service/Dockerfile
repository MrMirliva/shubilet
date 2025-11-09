# -------- Stage 1: Build --------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy project
COPY . .

# Build the application (skip tests for faster image builds)
RUN [ -f "./mvnw" ] && ./mvnw -q clean package -DskipTests || mvn -q clean package -DskipTests

# -------- Stage 2: Run --------
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy the fat JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# The default application port (adjust if different in your project)
EXPOSE 8081

# ----------------------
# Eureka + Postgres Config (override via docker-compose)
# ----------------------

# Eureka server discovery URL (referencing eureka-server container)
ENV EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/

# Activate docker profile if you create one
ENV SPRING_PROFILES_ACTIVE=docker

# PostgreSQL database connection (will be overridden in compose)
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/member_db
ENV SPRING_DATASOURCE_USERNAME=member_user
ENV SPRING_DATASOURCE_PASSWORD=changeme
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update

ENTRYPOINT ["java", "-jar", "app.jar"]
