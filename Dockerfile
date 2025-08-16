# =================================================================
# 1단계: 프로젝트 빌드 (Builder Stage)
# =================================================================
# ✅ 수정: 올바른 이미지 이름 (amazon/ 접두사, -jdk 태그 추가)
FROM amazon/amazoncorretto:17-alpine-jdk AS builder

WORKDIR /app

# Gradle 관련 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# ✅ gradlew 파일에 실행 권한 부여
RUN chmod +x ./gradlew

# 소스 코드 복사
COPY src src

# Gradle로 프로젝트 빌드
RUN ./gradlew bootJar -x test


# =================================================================
# 2단계: 최종 이미지 생성 (Final Stage)
# =================================================================
# ✅ 수정: 올바른 이미지 이름 (amazon/ 접두사, -jre 태그 추가)
FROM amazon/amazoncorretto:17-alpine-jre

WORKDIR /app

# 1단계에서 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]