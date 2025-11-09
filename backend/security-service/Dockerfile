# ---- Build stage ----
# Uses Maven with Temurin JDK 21 to compile the app
FROM maven:3.9.8-eclipse-temurin-21 AS build

WORKDIR /app
# Copy pom first to leverage layer caching
COPY pom.xml ./
# Download dependencies (better caching)
RUN mvn -q -e -DskipTests dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn -q -DskipTests package

# ---- Run stage ----
# Use a small JRE 21 image for runtime
FROM eclipse-temurin:21-jre

# Create non-root user for security
RUN useradd -ms /bin/bash appuser
WORKDIR /app

# Copy the fat JAR from the build stage
# Copies whatever jar was produced (e.g., security-service-0.0.1-SNAPSHOT.jar)
COPY --from=build /app/target/*.jar /app/app.jar

# Environment defaults (override via docker-compose)
ENV SPRING_PROFILES_ACTIVE=docker \
    SERVER_PORT=8084 \
    JAVA_OPTS=""

# Healthcheck hits Spring Boot Actuator
# Make sure you have actuator dependency and /actuator/health enabled
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:${SERVER_PORT}/actuator/health | grep -q '"status":"UP"' || exit 1

# Expose service port
EXPOSE 8084

# Entry point
ENTRYPOINT ["/bin/sh","-c","java $JAVA_OPTS -jar /app/app.jar --server.port=${SERVER_PORT}"]
