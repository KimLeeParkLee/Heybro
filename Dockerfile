# =================================================================
# 1단계: 프로젝트 빌드 (Builder Stage)
# =================================================================
# Java 17 JDK가 설치된 이미지를 기반으로 빌드 환경을 구성합니다.
FROM amazoncorretto:17-alpine AS builder

# 작업 디렉터리를 /app으로 설정합니다.
WORKDIR /app

# Gradle Wrapper 파일을 먼저 복사하여 종속성 캐싱을 활용합니다.
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

# gradlew 실행 권한 부여
RUN chmod +x gradlew

# build.gradle 파일을 복사합니다.
COPY build.gradle .

# 소스 코드를 복사합니다.
COPY src src

# Gradle을 사용하여 프로젝트를 빌드합니다. ('bootJar'는 Spring Boot 실행 가능 JAR를 만듭니다)
# 테스트는 CI/CD 파이프라인의 별도 단계에서 수행하는 것이 일반적이므로 건너뜁니다.
RUN ./gradlew bootJar -x test


# =================================================================
# 2단계: 최종 이미지 생성 (Final Stage)
# =================================================================
# Java 17 JRE(실행 환경)만 포함된 훨씬 가벼운 이미지를 기반으로 합니다.
FROM amazoncorretto:17-alpine

# 작업 디렉터리를 /app으로 설정합니다.
WORKDIR /app

# 1단계(builder)에서 생성된 JAR 파일을 복사해옵니다.
# build/libs/ 폴더 안에 있는 .jar 파일을 app.jar 라는 이름으로 복사합니다.
COPY --from=builder /app/build/libs/*.jar app.jar

# 컨테이너 외부로 8080 포트를 노출합니다.
EXPOSE 8080

# 컨테이너가 시작될 때 JAR 파일을 실행하는 명령어를 설정합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]