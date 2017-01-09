package com.codingbash.responder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Component;

// TODO: Will need to refactor how the mention differentiation is achieved
@Lazy
@Component
public class MemeResponseProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemeResponseProcessor.class);

	@Autowired
	private MemeResponder memeResponder;

	@Autowired
	private SubscriptionResponder subscriptionResponder;

	@Autowired
	@Qualifier("homeTweets")
	private List<Tweet> homeTweets;

	public void processMentions(List<Tweet> mentions) {
		LOGGER.info("< #processMentions: mentions.size()={}", mentions.size());

		for (Tweet mention : mentions) {
			/*
			 * Processor
			 */
			if (mention.getText().toLowerCase().contains(" sub")) {
				subscriptionResponder.createSubscriptionConfirmResponse(mention);
			} else {
				memeResponder.createMemeResponse(mention.getFromUser(), mention.getId());
			}

		}

	}
}
