# Base Image
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사
COPY build/libs/friendchise-0.0.1-SNAPSHOT.jar /app/friendchise.jar

# 실행
CMD ["java", "-jar", "friendchise.jar"]