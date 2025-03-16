# Base image
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /puzzlelog_back

# .env 파일 복사 (환경 변수 설정)
COPY /puzzlelog_back/.env .env

# JAR 파일 복사
ARG JAR_FILE=build/libs/puzzlelog-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /puzzlelog_back/app.jar

# 실행 명령어
ENTRYPOINT ["java", "-jar", "/puzzlelog_back/app.jar"]
