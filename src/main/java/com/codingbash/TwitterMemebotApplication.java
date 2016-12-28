package com.codingbash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.social.config.annotation.EnableSocial;

@EnableScheduling
@EnableSocial
@EnableAsync
@SpringBootApplication
public class TwitterMemebotApplication {
	public static void main(String[] args) {
		SpringApplication.run(TwitterMemebotApplication.class, args);
	}
}
