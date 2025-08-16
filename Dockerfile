# =================================================================
# 1단계: 프로젝트 빌드 (Builder Stage)
# =================================================================
FROM amazoncorretto:17-alpine AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x ./gradlew

COPY src src

RUN ./gradlew bootJar -x test


# =================================================================
# 2단계: 최종 이미지 생성 (Final Stage)
# =================================================================
FROM amazoncorretto:17-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
