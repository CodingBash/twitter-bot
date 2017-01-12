package com.codingbash.responder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.TweetData;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;

import com.codingbash.model.TweetDataPayload;

/*
 * TODO: DevelopmentImpl needs to mimic this class more. Too often does this class fail in the production environment
 */
@Component
@Profile({ "production" })
public class MemeSenderProductionImpl implements MemeSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemeSenderProductionImpl.class);

	@Autowired
	private Twitter twitter;

	@Override
	public Tweet sendTweet(TweetDataPayload payload) {
		LOGGER.info("< Sending tweet: payload.getInReplyToStatusId={}, payload.getMessage()={}",
				payload.getInReplyToStatusId(), payload.getMessage());
		TweetData tweetData = new TweetData(payload.getMessage());
		if (payload.getInReplyToStatusId() != null) {
			tweetData.inReplyToStatus(payload.getInReplyToStatusId());
		}
		if (payload.getResourceMediaList() != null) {
			for (Resource resource : payload.getResourceMediaList()) {
				tweetData.withMedia(resource);
			}
		}
		Tweet sentTweet = twitter.timelineOperations().updateStatus(tweetData);
		LOGGER.info("> Sent tweet: sentTweet.getId()={}", sentTweet.getId());
		return sentTweet;
	}
}
