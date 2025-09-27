# Stage 1: Build
FROM maven:3.9.9-amazoncorretto-21 AS build
WORKDIR /app
# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B
# Copy source and build
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Runtime
FROM amazoncorretto:21-alpine
WORKDIR /app
# Create logs directory and set permissions
RUN mkdir -p /app/logs && \
    addgroup -S appgroup && \
    adduser -S appuser -G appgroup && \
    chown -R appuser:appgroup /app/logs
# Copy specific JAR
COPY --from=build /app/target/human.resource-0.0.1-SNAPSHOT.jar app.jar
# Expose port
EXPOSE 8080
# Run as non-root user
USER appuser
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]