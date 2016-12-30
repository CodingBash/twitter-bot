package com.codingbash;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;

@Lazy
@Component
@Profile({ "development", "production" })
public class MemeMentionsRetrieverImpl implements MemeMentionsRetriever {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemeMentionsRetrieverImpl.class);

	@Autowired
	private Twitter twitter;

	private Long lastTweetId = null;
	private static final int maxPageSize = 20;
	private static final long maxTweetId = 0;

	@Override
	public List<Tweet> retrieveMentions() {
		LOGGER.info("Mentions being retrieved");
		if (twitter.isAuthorized()) {
			/*
			 * Retrieve latest mentions
			 */
			List<Tweet> homeTweets = (lastTweetId != null)
					? twitter.timelineOperations().getMentions(maxPageSize, lastTweetId, maxTweetId)
					: twitter.timelineOperations().getMentions();

			/*
			 * Set the most recent tweet ID
			 */
			if (homeTweets.size() > 0) {
				lastTweetId = homeTweets.get(0).getId();
			}

			return homeTweets;
		}

		return new ArrayList<Tweet>();
	}
}
