package com.codingbash;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;

@Component
public class TwitterLogic {
	
	@Autowired
	private Twitter twitter;
	
	@Scheduled(fixedRate = 5000)
	public void postTweets() {
		System.out.println("Posting Tweets:");
		if (twitter.isAuthorized()) {
			System.out.println("-- Authorized");
			List<Tweet> homeTweets = twitter.timelineOperations().getHomeTimeline();

			for (Tweet tweet : homeTweets) {
				System.out.println(tweet.getText());
			}
		}
	}
}
