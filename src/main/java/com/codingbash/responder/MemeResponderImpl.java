package com.codingbash.responder;

import static com.codingbash.constant.MemeConstants.CUSTOM_MESSAGE_ARRAY;
import static com.codingbash.constant.MemeConstants.MAX_WAIT_RESPONSE_TIME_IN_MS;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
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
	private Queue<TweetDataPayload> postTweetQueue;

	@Autowired
	@Qualifier("homeTweets")
	private List<Tweet> homeTweets;

	@Override
	public void replyToMentions(List<Tweet> mentions, List<Tweet> memeArchive) {
		LOGGER.info("< #replyToMentions: mentions.size()={}, memeArchive.size()={}, homeTweets.size()={}",
				mentions.size(), memeArchive.size(), homeTweets.size());

		mentions = removeDuplicates(mentions);

		LOGGER.info("< Responding to each mention: mentions.size()={}", mentions.size());
		TweetDataPayload payload;
		for (Tweet mention : mentions) {
			payload = new TweetDataPayload();
			payload.setMessage(constructReplyMessage(mention, memeArchive));
			payload.setInReplyToStatusId(mention.getId());

			boolean addedSuccessfully = false;
			do {
				addedSuccessfully = postTweetQueue.offer(payload);
				if (addedSuccessfully == false) {
					LOGGER.warn("<> UNABLE TO POLL QUEUE, SLEEPING FOR {}", MAX_WAIT_RESPONSE_TIME_IN_MS);
					try {
						Thread.sleep(MAX_WAIT_RESPONSE_TIME_IN_MS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} while (addedSuccessfully == false);

		}

	}

	public List<Tweet> removeDuplicates(List<Tweet> mentions) {
		LOGGER.info("< #removeDuplicates(): mentions.size()={}", mentions.size());

		if (homeTweets.isEmpty()) {
			LOGGER.info("< homeTweets empty - currently retrieving");
			homeTweets.addAll(twitter.timelineOperations().getUserTimeline(200));
			LOGGER.info("> Retrieved homeTweets: homeTweets.size()={}", homeTweets.size());
		}

		List<Tweet> sanitizedMentions = new ArrayList<Tweet>(mentions.size());

		mentionIteration: for (Tweet mention : mentions) {
			LOGGER.info("<> Checking for duplicates");
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

		LOGGER.info("> #removeDuplicates(): sanitizedMentions.size()={}", sanitizedMentions.size());
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
