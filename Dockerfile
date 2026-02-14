# syntax=docker/dockerfile:1

FROM --platform=linux/amd64 gradle:8.11.1-jdk21-alpine AS builder
WORKDIR /app

COPY gradle gradle
COPY gradlew build.gradle settings.gradle ./
COPY src src

RUN gradle clean build -x test --no-daemon

FROM --platform=linux/amd64 eclipse-temurin:21-jre-alpine
WORKDIR /app

ARG PORT=7000
ENV PORT=${PORT}
ENV LOG_DIR=/app/logs

RUN mkdir -p "$LOG_DIR"
VOLUME ["/app/logs"]

COPY --from=builder /app/build/libs/app-1.0.0-all.jar /app/app.jar

EXPOSE ${PORT}

CMD ["sh", "-c", "java -jar /app/app.jar 2>&1 | tee -a ${LOG_DIR}/application.log"]
