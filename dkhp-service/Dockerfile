FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/dkhp-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8085


