# 빌드 스테이지 (Spring Boot Jar 생성)
FROM eclipse-temurin:21-jdk-alpine AS builder

# 작업 디렉토리를 Spring 프로젝트 폴더로 설정
WORKDIR /CodeJavaApp

# Gradle 설정 파일 복사
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .

# 소스 코드 복사
COPY src/ src/

# Jar 파일 빌드
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# 실행 스테이지 (최종 이미지)
FROM eclipse-temurin:21-jre-alpine

# Jar 파일을 찾을 경로 설정
WORKDIR /app

# 빌드 스테이지에서 생성된 Jar 파일 복사 (경로: CodeJavaApp/build/libs/CodeJavaApp-0.0.1-SNAPSHOT.jar)
COPY --from=builder /CodeJavaApp/build/libs/*.jar CodeJavaApp.jar

# Jar 파일을 실행할 때 정적 파일이 포함된 상태로 실행됩니다.
WORKDIR /app

# 기본 포트 설정 (Render 환경에 맞춰 10000으로 설정)
EXPOSE 10000

# Spring Boot 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "CodeJavaApp.jar"]
