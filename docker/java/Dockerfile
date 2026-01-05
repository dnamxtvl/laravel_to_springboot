# Build stage
FROM eclipse-temurin:21-jdk-alpine as builder

WORKDIR /workspace

# 1. Copy Maven Wrapper trước để có thể chạy mvnw
COPY .mvn .mvn
COPY mvnw .

# 2. Copy pom.xml riêng để tận dụng cache layer
COPY pom.xml .

# 3. Cấp quyền thực thi cho mvnw và tải dependencies
RUN chmod +x mvnw && \
    ./mvnw dependency:go-offline -B

# 4. Copy toàn bộ source code
COPY src ./src

# 5. Build ứng dụng và giải nén resources
RUN ./mvnw clean package -DskipTests && \
    mkdir -p /workspace/target/extracted && \
    unzip -q target/*.jar -d /workspace/target/extracted

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy JAR và resources từ builder
COPY --from=builder /workspace/target/*.jar app.jar
COPY --from=builder /workspace/target/extracted/BOOT-INF/classes/db /app/resources/db

# Cài đặt các công cụ cần thiết
RUN apk add --no-cache \
    mysql-client \
    busybox-extras

ENTRYPOINT ["java", "-jar", "app.jar"]