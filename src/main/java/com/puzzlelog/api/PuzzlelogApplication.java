package com.puzzlelog.api;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Puzzlelog 애플리케이션의 진입점입니다.
 *
 * .env 파일에서 환경 변수를 로드하여 Java 시스템 환경변수로 등록한 후,
 * Spring Boot 애플리케이션을 실행합니다.
 */
@Slf4j
@SpringBootApplication
public class PuzzlelogApplication {

    public static void main(String[] args) {

        // .env 파일에서 환경 변수 로드
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir")) // 프로젝트 루트 디렉토리에서 탐색
                .filename(".env")                          // 사용할 환경 변수 파일명
                .ignoreIfMissing()                         // .env 파일이 없으면 무시
                .load();

        // 로드된 환경 변수를 Java 시스템 프로퍼티로 등록
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
            log.info("✅ 환경변수 로드: {} = {}", entry.getKey(), entry.getValue());
        });

        // Spring Boot 애플리케이션 실행
        SpringApplication.run(PuzzlelogApplication.class, args);
    }
}
