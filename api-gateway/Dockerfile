FROM openjdk:21-jre-slim

WORKDIR /app

COPY target/api-gateway-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]