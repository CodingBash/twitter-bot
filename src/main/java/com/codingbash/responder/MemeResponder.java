package com.codingbash.responder;

import static com.codingbash.constant.MemeConstants.CUSTOM_MESSAGE_ARRAY;

import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.stereotype.Component;

import com.codingbash.MemeUtility;
import com.codingbash.model.MemeAccount;
import com.codingbash.model.TweetDataPayload;

@Component
public class MemeResponder {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemeResponder.class);

	@Autowired
	private Queue<TweetDataPayload> postTweetQueue;

	@Autowired
	@Qualifier("memeArchive")
	private List<Tweet> memeArchive;

	@Autowired
	private MemeUtility memeUtility;

	// TODO: Async?
	public void createMemeResponse(String screenName, Long inReplyToStatusId) {
		while (memeArchive.size() == 0) {
			memeUtility.reloadTheMemes();
		}

		TweetDataPayload payload;
		payload = new TweetDataPayload();
		payload.setMessage(constructReplyMessage(screenName));
		payload.setInReplyToStatusId(inReplyToStatusId);

		memeUtility.addPayloadToQueue(payload);
	}
	
	public String constructReplyMessage(String screenName) {
		String username = "@" + screenName;
		int randomMemeIndex = (int) (Math.random() * ((double) memeArchive.size()));
		int randomCustomMessageIndex = (int) (Math.random() * ((double) CUSTOM_MESSAGE_ARRAY.length));
		Tweet memeTweet = memeArchive.get(randomMemeIndex);
		String memeLink = String.format("https://twitter.com/%s/status/%s", memeTweet.getFromUser(),
				memeTweet.getIdStr());
		String message = username + " " + CUSTOM_MESSAGE_ARRAY[randomCustomMessageIndex] + " " + memeLink;
		return message;
	}

	public String constructReplyMessage(Tweet mention, List<Tweet> memeArchive) {
		String username = "@" + mention.getFromUser();
		int randomMemeIndex = (int) (Math.random() * ((double) memeArchive.size()));
		int randomCustomMessageIndex = (int) (Math.random() * ((double) CUSTOM_MESSAGE_ARRAY.length));
		Tweet memeTweet = memeArchive.get(randomMemeIndex);
		String memeLink = String.format("https://twitter.com/%s/status/%s", memeTweet.getFromUser(),
				memeTweet.getIdStr());
		String message = username + " " + CUSTOM_MESSAGE_ARRAY[randomCustomMessageIndex] + " " + memeLink;
		return message;
	}

}
