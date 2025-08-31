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
# Copy specific JAR
COPY --from=build /app/target/human.resource-0.0.1-SNAPSHOT.jar app.jar
# Expose port
EXPOSE 8080
# Optional: Run as non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]