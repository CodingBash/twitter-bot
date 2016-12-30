package com.codingbash;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

@Configuration
public class MemeConfiguration {

	@Bean
	@Lazy
	@Profile("development")
	public Twitter twitterDevelopment(@Value("${spring.social.twitter.appId}") String consumerKey,
			@Value("${spring.social.twitter.appSecret}") String consumerSecret,
			@Value("${spring.social.twitter.access-token}") String accessToken,
			@Value("${spring.social.twitter.access-token-secret}") String accessTokenSecret) {
		return new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);
	}

	@Bean
	@Lazy
	@Autowired
	@Profile("production")
	public Twitter twitterProduction(Environment env){
		String consumerKey = env.getProperty("spring.social.twitter.appId");
		String consumerSecret = env.getProperty("spring.social.twitter.appSecret");
		String accessToken = env.getProperty("spring.social.twitter.access-token");
		String accessTokenSecret = env.getProperty("spring.social.twitter.access-token-secret");
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
