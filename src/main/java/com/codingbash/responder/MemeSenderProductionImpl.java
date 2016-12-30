package com.codingbash.responder;

import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.TweetData;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;

import com.codingbash.model.TweetDataPayload;

@Component
@Profile({ "production" })
public class MemeSenderProductionImpl implements MemeSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemeSenderProductionImpl.class);

	@Autowired
	private Twitter twitter;

	@Override
	public Tweet sendTweet(TweetDataPayload payload) {
		LOGGER.debug("< Sending tweet: payload.getInReplyToStatusId{}, payload.getMessage()={}",
				payload.getInReplyToStatusId(), payload.getMessage());

		TweetData tweetData = new TweetData(payload.getMessage()).inReplyToStatus(payload.getInReplyToStatusId());

		Tweet sentTweet = twitter.timelineOperations().updateStatus(tweetData);

		LOGGER.debug("> Sent tweet: sentTweet.getId()={}", sentTweet.getId());
		return sentTweet;
	}
}
