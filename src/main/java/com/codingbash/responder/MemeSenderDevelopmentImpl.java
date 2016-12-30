package com.codingbash.responder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Component;

import com.codingbash.model.TweetDataPayload;

@Component
@Profile({ "development" })
public class MemeSenderDevelopmentImpl implements MemeSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemeSenderDevelopmentImpl.class);

	@Override
	public Tweet sendTweet(TweetDataPayload payload) {
		LOGGER.info("< Sending tweet: payload.getInReplyToStatusId{}, payload.getMessage()={}",
				payload.getInReplyToStatusId(), payload.getMessage());
		System.out.printf("SENDING TWEET: statusId=%s, message=%s%n", payload.getInReplyToStatusId(),
				payload.getMessage());
		LOGGER.info("> Sent tweet");
		return null;
	}

}
