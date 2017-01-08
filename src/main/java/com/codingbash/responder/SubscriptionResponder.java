package com.codingbash.responder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Component;

import com.codingbash.MemeUtility;
import com.codingbash.constant.SubscriptionResult;
import com.codingbash.model.TweetDataPayload;
import com.codingbash.repository.MemeRepository;

@Component
public class SubscriptionResponder {

	private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionResponder.class);

	@Autowired
	private MemeRepository memeRepository;

	@Autowired
	private MemeUtility memeUtility;

	public void createSubscriptionConfirmResponse(Tweet mention) {
		LOGGER.info("< #createSubscriptionConfirmResponse(): mention.getText()={}", mention.getText());

		SubscriptionResult subscriptionResult = memeRepository.registerSubscriptionStatus(mention.getUser());

		TweetDataPayload payload;
		payload = new TweetDataPayload();
		payload.setInReplyToStatusId(mention.getId());
		payload.setMessage(constructSubscriptionReplyMessage(mention, subscriptionResult));

		memeUtility.addPayloadToQueue(payload);

		LOGGER.info("> #createSubscriptionConfirmResponse(): registrationResult={}", subscriptionResult);
	}

	private String constructSubscriptionReplyMessage(Tweet mention, SubscriptionResult subscriptionResult) {
		String username = "@" + mention.getFromUser();
		String message = "";
		switch (subscriptionResult) {
		case SUBSCRIPTION_SUCCESS:
			message = "You are now subscribed for daily memes.";
			break;
		case ALREADY_SUBSCRIBE:
			message = "You are already subscribed.";
			break;
		case NEW_ACCOUNT_REGISTRATION_ERROR:
		case SUBSCRIPTION_REGISTRATION_ERROR:
			message = "Unable to subscribe.";
			break;

		}
		String result = username + " " + message;
		return result;
	}
}
