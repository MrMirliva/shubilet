# -------- Stage 1: Build (Maven image, cached deps) --------
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml ./
RUN mvn -q -e -DskipTests dependency:go-offline

# Copy sources and build
COPY src ./src
RUN mvn -q -DskipTests package

# -------- Stage 2: Run (JRE) --------
FROM eclipse-temurin:21-jre
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
