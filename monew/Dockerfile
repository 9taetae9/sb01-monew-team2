FROM amazoncorretto:17-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon

COPY src/ src/
RUN ./gradlew clean build -x test --no-daemon

FROM amazoncorretto:17-alpine
WORKDIR /app

ENV PROJECT_NAME=monew
ENV PROJECT_VERSION=v1.0.0
ENV JAVA_OPTS="-Xmx350m -Xms256m -XX:MaxMetaspaceSize=128m -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/logs/heapdump.hprof"

RUN mkdir -p /app/logs && chmod 755 /app/logs

COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar
EXPOSE 80

ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar app.jar --server.port=80 --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}"]
