FROM openjdk:21-jdk-slim

WORKDIR /app


COPY target/accounts-0.0.1-SNAPSHOT.jar accounts-0.0.1-SNAPSHOT.jar

# Expose port 8080
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/accounts-0.0.1-SNAPSHOT.jar"]