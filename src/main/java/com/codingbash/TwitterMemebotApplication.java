package com.codingbash;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

@SpringBootApplication
@EnableScheduling
@EnableSocial
public class TwitterMemebotApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwitterMemebotApplication.class, args);
	}

	@Value("${spring.social.twitter.appId}")
	private String consumerKey;

	@Value("${spring.social.twitter.appSecret}")
	private String consumerSecret;

	@Value("${spring.social.twitter.access-token}")
	private String accessToken;

	@Value("${spring.social.twitter.access-token-secret}")
	private String accessTokenSecret;

	@Bean
	public Twitter twitter() {
		return new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);
	}


}
