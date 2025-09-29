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

# OS 시간대 설정
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
    && echo "Asia/Seoul" > /etc/timezone \
    && apk del tzdata

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# JVM 시간대 옵션 추가
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]