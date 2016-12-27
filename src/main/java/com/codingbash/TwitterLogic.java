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

	private Long lastTweetId = null;
	private int maxPageSize = 200;
	private long maxTweetId = Long.MAX_VALUE;

	@Scheduled(fixedRate = 5000)
	public void postTweets() {
		System.out.println("Posting Tweets:");
		if (twitter.isAuthorized()) {
			System.out.println("-- Authorized");
			List<Tweet> homeTweets = (lastTweetId != null)
					? twitter.timelineOperations().getMentions(20, lastTweetId, 0)
					: twitter.timelineOperations().getMentions();
			if (homeTweets.size() > 0) {
				lastTweetId = homeTweets.get(0).getId();
				
				System.out.println("Assigned LastTweetId" + lastTweetId.toString() + ": " + homeTweets.get(0).getText());
			}
			for (Tweet tweet : homeTweets) {
				System.out.println(tweet.getText());
			}
		}
	}
}
