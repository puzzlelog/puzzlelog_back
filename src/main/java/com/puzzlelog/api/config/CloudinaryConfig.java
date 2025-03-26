package com.puzzlelog.api.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cloudinary 설정 클래스입니다.
 * 애플리케이션에서 Cloudinary 서비스를 사용할 수 있도록 빈을 등록합니다.
 */
@Configuration
public class CloudinaryConfig {

    /**
     * Cloudinary 빈 생성
     *
     * 환경 변수(CLOUDINARY_URL)를 이용하여 Cloudinary 인스턴스를 생성합니다.
     * CLOUDINARY_URL 형식 예시: cloudinary://<api_key>:<api_secret>@<cloud_name>
     *
     * @return 설정된 Cloudinary 객체
     * @throws RuntimeException CLOUDINARY_URL 환경 변수가 없거나 잘못 설정된 경우 발생
     */
	@Bean
	public Cloudinary cloudinary() {
	    String cloudinaryUrl = System.getProperty("CLOUDINARY_URL");
	    if (cloudinaryUrl == null || cloudinaryUrl.isEmpty()) {
	        cloudinaryUrl = System.getenv("CLOUDINARY_URL");
	    }

	    if (cloudinaryUrl == null || cloudinaryUrl.isEmpty()) {
	        throw new RuntimeException("❌ CLOUDINARY_URL 환경 변수가 설정되지 않았습니다.");
	    }

	    return new Cloudinary(cloudinaryUrl);
	}
}
