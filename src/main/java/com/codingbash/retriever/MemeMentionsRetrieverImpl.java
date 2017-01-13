package com.codingbash.retriever;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.social.twitter.api.ResourceFamily;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;

import com.codingbash.MemeUtility;

@Lazy
@Component
public class MemeMentionsRetrieverImpl implements MemeMentionsRetriever {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemeMentionsRetrieverImpl.class);

	@Autowired
	private Twitter twitter;
	
	@Autowired
	private MemeUtility utility;

	@Autowired
	@Qualifier("lastTweetId")
	private Long lastTweetId;
	
	private static final int maxPageSize = 20;
	private static final long maxTweetId = 0;

	@Override
	public List<Tweet> retrieveMentions() {
		LOGGER.info("< #retrieveMentions() - Mentions being retrieved: lastTweetId={}", lastTweetId);
		if (twitter.isAuthorized()) {
			utility.checkRateLimit(ResourceFamily.STATUSES, "/statuses/mentions_timeline");
			List<Tweet> allMentions = (lastTweetId != null)
					? twitter.timelineOperations().getMentions(maxPageSize, lastTweetId, maxTweetId)
					: twitter.timelineOperations().getMentions();
			/*
			 * Set the most recent tweet ID
			 */
			if (allMentions.size() > 0) {
				lastTweetId = allMentions.get(0).getId();
			}
			
			LOGGER.info("> #retrieveMentions() - ");
			return allMentions;
		}
		LOGGER.warn("> #retrieveMentions() - Twitter object not authorized!");
		return new ArrayList<Tweet>();
	}
}
