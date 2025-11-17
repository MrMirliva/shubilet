# -------- Stage 1: Build (Maven + cached deps) --------
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
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update

ENTRYPOINT ["java", "-jar", "app.jar"]
