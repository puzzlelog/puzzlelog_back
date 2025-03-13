package com.puzzlelog.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing // @CreatedDate 붙은 필드에 시간 들어가게 하는 형태
public class MongoConfig {
	
}
