package com.puzzlelog.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PuzzlelogApplication {

	public static void main(String[] args) {
		SpringApplication.run(PuzzlelogApplication.class, args);
	}
	
	@Bean
    public ServletWebServerFactory servletWebServerFactory(){
        return new org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory();
    }

}
