# Sử dụng OpenJDK 21
FROM openjdk:21-jdk-slim

# Thiết lập thư mục làm việc
WORKDIR /app

# Copy file JAR từ target (sau khi build)
COPY target/khht-service.jar app.jar

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]

# Mở cổng 8082
EXPOSE 8083
