FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/loan-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8100

ENTRYPOINT ["java", "-jar", "app.jar"]