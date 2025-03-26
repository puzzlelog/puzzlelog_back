package com.puzzlelog.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * MongoDB 설정 클래스입니다.
 * 
 * MongoDB의 Auditing 기능을 활성화하여,
 * 도메인 클래스의 @CreatedDate 및 @LastModifiedDate 어노테이션이 붙은 필드에
 * 자동으로 시간값을 주입할 수 있게 합니다.
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
    // 현재로서는 별도의 설정이 필요하지 않습니다.
}
