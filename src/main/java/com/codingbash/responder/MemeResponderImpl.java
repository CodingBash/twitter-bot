package com.codingbash.responder;

import static com.codingbash.constant.MemeConstants.CUSTOM_MESSAGE_ARRAY;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;

import com.codingbash.model.TweetDataPayload;

@Lazy
@Component
public class MemeResponderImpl implements MemeResponder {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemeResponderImpl.class);

	@Autowired
	private Twitter twitter;

	@Autowired
	private MemeSender sender;

	private List<Tweet> homeTweets = new ArrayList<Tweet>();

	@Async
	@Override
	public void replyToMentions(List<Tweet> mentions, List<Tweet> memeArchive) {
		LOGGER.debug("< #replyToMentions: mentions.size()={}, memeArchive.size()={}, homeTweets.size()={}",
				mentions.size(), memeArchive.size(), homeTweets.size());

		mentions = removeDuplicates(mentions);

		LOGGER.debug("< Responding to each mention: mentions.size()={}", mentions.size());
		TweetDataPayload payload;
		for (Tweet mention : mentions) {
			payload = new TweetDataPayload();
			payload.setMessage(constructReplyMessage(mention, memeArchive));
			payload.setInReplyToStatusId(mention.getId());
			sender.sendTweet(payload);
		}
	}

	public List<Tweet> removeDuplicates(List<Tweet> mentions) {
		LOGGER.debug("< #removeDuplicates(): mentions.size()={}", mentions.size());

		if (homeTweets.isEmpty()) {
			LOGGER.debug("< homeTweets empty - currently retrieving");
			homeTweets.addAll(twitter.timelineOperations().getUserTimeline(200));
			LOGGER.debug("> Retrieved homeTweets: homeTweets.size()={}", homeTweets.size());
		}

		List<Tweet> sanitizedMentions = new ArrayList<Tweet>(mentions.size());

		mentionIteration: for (Tweet mention : mentions) {
			LOGGER.debug("<> Checking for duplicates");
			homeIteration: for (Tweet homeTweet : homeTweets) {
				Long replyStatusId = homeTweet.getInReplyToStatusId();
				if (mention.getId() == homeTweet.getId()) {
					LOGGER.info("<> Discovered self-mentioned tweet - EXCLUDING MENTION");
					continue mentionIteration;
				} else if (null == replyStatusId) {
					LOGGER.info("<> Discovered no-reply self tweet - SKIPPING CHECK");
					continue homeIteration;
				} else if (mention.getId() == replyStatusId) {
					LOGGER.info("<> Duplicate response detected - EXCLUDING MENTION: mention.getId()={}",
							mention.getIdStr());
					continue mentionIteration;
				}
			}
			sanitizedMentions.add(mention);
		}

		LOGGER.debug("> #removeDuplicates(): sanitizedMentions.size()={}", sanitizedMentions.size());
		return sanitizedMentions;
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
