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

# Service port inside container
EXPOSE 8081

# Defaults (can be overridden by compose)
ENV EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]

