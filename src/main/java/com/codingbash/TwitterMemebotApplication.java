package com.codingbash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.social.TwitterAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.social.config.annotation.EnableSocial;

@EnableAsync
@EnableSocial
@EnableScheduling
@EnableMongoRepositories
@SpringBootApplication
@EnableAutoConfiguration(exclude = { TwitterAutoConfiguration.class })
public class TwitterMemebotApplication {
	public static void main(String[] args) {
		SpringApplication.run(TwitterMemebotApplication.class, args);
	}
}
