FROM openjdk:21-jdk-slim


RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy the jar file
COPY target/eureka-server-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8761

# Add health check endpoint
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8761/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]