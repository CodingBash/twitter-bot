package com.codingbash;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

@Configuration
public class MemeConfiguration {

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
	
	@Bean
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("MemeResponse-");
		executor.initialize();
		return executor;
	}
}
