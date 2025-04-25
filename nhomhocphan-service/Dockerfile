#FROM openjdk:21-jdk-slim
#WORKDIR /app
#COPY target/nhomhocphan-service-0.0.1-SNAPSHOT.jar app.jar
#EXPOSE 8084
#ENTRYPOINT ["java", "-jar", "/app/app.jar"]
# Sử dụng OpenJDK 21
FROM openjdk:21-jdk-slim

# Thiết lập thư mục làm việc
WORKDIR /app

# Copy file JAR từ target (sau khi build)
COPY target/nhomhocphan-service.jar app.jar

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]

# Mở cổng 8082
EXPOSE 8084
