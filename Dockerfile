# 1️⃣ 构建阶段 (Builder) - 使用 Maven + JDK 21
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# 先复制 pom.xml，利用 Docker 缓存依赖
COPY pom.xml .
RUN mvn dependency:go-offline

# 复制源码并打包
COPY src ./src
RUN mvn clean package -DskipTests

# 2️⃣ 运行阶段 (Runtime) - 使用精简的 JDK 21 镜像
# 推荐使用 eclipse-temurin:21-jdk-alpine 体积小
# 如果希望更小的运行时，也可以使用 jre 版本，但 JDK 21 的 jre 镜像不常见，直接用 jdk 也行
FROM eclipse-temurin:21-jdk-alpine

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app

# 从构建阶段复制 jar 包
COPY --from=builder /app/target/*.jar ./app.jar

# 启动命令（使用分层技术，如果 Spring Boot 版本支持）
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

# 暴露端口（修改为你的实际端口，默认 8080）
EXPOSE 8080

# 可选健康检查（需要引入 spring-boot-starter-actuator）
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1