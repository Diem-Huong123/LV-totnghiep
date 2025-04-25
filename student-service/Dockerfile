FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/student-service.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

