package com.codingbash.responder;

import static com.codingbash.constant.MemeConstants.CUSTOM_MESSAGE_ARRAY;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.social.twitter.api.MediaEntity;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Component;

import com.codingbash.MemeUtility;
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

		TweetDataPayload payload = constructTweetDataPayload(screenName, inReplyToStatusId);

		memeUtility.addPayloadToQueue(payload);
	}

	public TweetDataPayload constructTweetDataPayload(String screenName, Long inReplyToStatusId) {
		TweetDataPayload payload = new TweetDataPayload();

		/*
		 * Retrieve proper meme TODO: Move this logic to the memeReloader
		 */
		Tweet memeTweet = null;
		boolean properMemeFound = false;
		while (!properMemeFound) {
			int randomMemeIndex = (int) (Math.random() * ((double) memeArchive.size()));
			memeTweet = memeArchive.get(randomMemeIndex);
			if (!memeTweet.getEntities().getMedia().isEmpty() && !memeTweet.getText().contains("@")
					&& !memeTweet.isRetweet()) {
				properMemeFound = true;
				memeArchive.remove(randomMemeIndex);
			}
		}

		/*
		 * Set resources
		 */
		List<MediaEntity> mediaList = memeTweet.getEntities().getMedia();
		List<Resource> payloadResource = new ArrayList<Resource>(mediaList.size());
		for (MediaEntity media : mediaList) {
			try {
				Resource resource = new UrlResource(media.getMediaUrl());
				payloadResource.add(resource);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		payload.setResourceMediaList(payloadResource);

		/*
		 * Set message
		 */
		int randomCustomMessageIndex = (int) (Math.random() * ((double) CUSTOM_MESSAGE_ARRAY.length));
		String customMessage = CUSTOM_MESSAGE_ARRAY[randomCustomMessageIndex].replace("{}",
				"@" + screenName);
		String memeMessage = memeTweet.getText();
		payload.setMessage(customMessage + System.getProperty("line.separator") + System.getProperty("line.separator")
				+ memeMessage);

		/*
		 * Set replyToStatusId
		 */
		payload.setInReplyToStatusId(inReplyToStatusId);

		return payload;
	}

	@Deprecated
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

	@Deprecated
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
