# syntax=docker/dockerfile:1

FROM --platform=linux/amd64 gradle:8.11.1-jdk21 AS builder
WORKDIR /app

COPY gradle gradle
COPY gradlew build.gradle settings.gradle gradle.properties* ./
COPY src src

# Fix para Windows line endings + permiso de ejecución
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Build (saltando tests/spotless para entrega rápida)
RUN ./gradlew clean build -x test -x spotlessJavaCheck -x spotlessCheck --no-daemon

FROM --platform=linux/amd64 eclipse-temurin:21-jre-alpine
WORKDIR /app

ENV PORT=7000
ENV LOG_DIR=/app/logs

RUN mkdir -p "$LOG_DIR"
VOLUME ["/app/logs"]

# Evita fallar si cambia versión/nombre del jar
COPY --from=builder /app/build/libs/*-all.jar /app/app.jar

EXPOSE 7000

CMD ["sh", "-c", "mkfifo /tmp/java.log.pipe; tee -a ${LOG_DIR}/application.log < /tmp/java.log.pipe & exec java -Dserver.port=${PORT} -jar /app/app.jar > /tmp/java.log.pipe 2>&1"]
