package com.chatop.My_Chatop_Api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.chatop.My_Chatop_Api")
@EnableJpaRepositories(basePackages = "com.chatop.My_Chatop_Api")
@EntityScan(basePackages = "com.chatop.My_Chatop_Api.models")
public class MyChatopApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyChatopApiApplication.class, args);
	}

}
