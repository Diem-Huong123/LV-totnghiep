#FROM openjdk:21-jdk-slim
#EXPOSE 8082
#ADD target/courses-service.jar courses-service.jar
#ENTRYPOINT ["java","-jar","courses-service.jar"]

#FROM openjdk:21-jdk-slim
#WORKDIR /app
#COPY target/courses-service-0.0.1-SNAPSHOT.jar app.jar
#EXPOSE 8082
#ENTRYPOINT ["java", "-jar", "app.jar"]

# Sử dụng OpenJDK 21
FROM openjdk:21-jdk-slim

# Thiết lập thư mục làm việc
WORKDIR /app

# Copy file JAR từ target (sau khi build)
COPY target/courses-service.jar app.jar

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]

# Mở cổng 8082
EXPOSE 8082
