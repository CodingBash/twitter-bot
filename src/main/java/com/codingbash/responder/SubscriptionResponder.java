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

	public void createSubscriptionActionConfirmResponse(Tweet mention, boolean willBeSubscribed) {
		LOGGER.info("< #createSubscriptionConfirmResponse(): mention.getText()={}", mention.getText());

		SubscriptionResult subscriptionResult = memeRepository.registerSubscriptionStatus(mention.getUser(),
				willBeSubscribed);

		TweetDataPayload payload;
		payload = new TweetDataPayload();
		payload.setInReplyToStatusId(mention.getId());
		payload.setMessage(constructSubscriptionReplyMessage(mention, subscriptionResult, willBeSubscribed));

		memeUtility.addPayloadToQueue(payload);

		LOGGER.info("> #createSubscriptionConfirmResponse(): registrationResult={}", subscriptionResult);
	}

	private String constructSubscriptionReplyMessage(Tweet mention, SubscriptionResult subscriptionResult,
			boolean willBeSubscribed) {
		String username = "@" + mention.getFromUser();
		String message = "";
		switch (subscriptionResult) {
		case SUBSCRIPTION_ACTION_SUCCESS:
			if (willBeSubscribed) {
				message = "You are now subscribed for daily memes";
			} else {
				message = "You are now unsubscribed :(";
			}
			break;
		case NO_ACTION_NECESSARY:
			if (willBeSubscribed) {
				message = "You are already subscribed";
			} else {
				message = "You are already unsubscribed";
			}
			break;
		case NEW_ACCOUNT_REGISTRATION_ERROR:
		case SUBSCRIPTION_ACTION_REGISTRATION_ERROR:
			if (willBeSubscribed) {
				message = "Unable to subscribe";
			} else {
				message = "Unable to unsubscribe";
			}
			break;

		}
		String result = username + " " + message;
		return result;
	}
}
