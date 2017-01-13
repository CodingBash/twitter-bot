package com.codingbash.scheduler;

import static com.codingbash.constant.MemeConstants.MAX_WAIT_RESPONSE_TIME_IN_MS;
import static com.codingbash.constant.MemeConstants.MIN_WAIT_RESPONSE_TIME_IN_MS;

import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Component;

import com.codingbash.MemeUtility;
import com.codingbash.model.PostTweetLimiter;
import com.codingbash.model.TweetDataPayload;
import com.codingbash.responder.MemeResponseProcessor;
import com.codingbash.responder.MemeSender;
import com.codingbash.retriever.MemeMentionsRetriever;

@Component
public class InputOutputScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(InputOutputScheduler.class);

	@Autowired
	private Queue<TweetDataPayload> postTweetQueue;

	@Autowired
	private MemeMentionsRetriever memeMentionsRetriever;

	@Autowired
	private MemeResponseProcessor memeResponder;

	@Autowired
	private MemeSender memeSender;

	@Autowired
	private PostTweetLimiter limiter;

	@Autowired
	private MemeUtility utility;

	@Autowired
	@Qualifier("homeTweets")
	private List<Tweet> homeTweets;

	// TODO: possible thread conflict??
	private boolean responseFlag = false;

	/**
	 * Retrieves next response in the queue and sends
	 */
	@Scheduled(fixedDelay = MIN_WAIT_RESPONSE_TIME_IN_MS)
	public void sendResponseTrigger() {
		TweetDataPayload payload = postTweetQueue.peek();
		LOGGER.info("<> Sending tweet: limiter.get()={}, postTweetQueue.size={}", limiter.get(), postTweetQueue.size());
		if (payload != null) {
			if (limiter.permit()) {
				homeTweets.add(memeSender.sendTweet(payload));
				postTweetQueue.remove();
			}
		} else {
			LOGGER.info("<> Post Tweet Queue is empty");
			responseFlag = true;
		}

	}

	/**
	 * Retrieves the mentions
	 */
	@Scheduled(fixedDelay = MAX_WAIT_RESPONSE_TIME_IN_MS)
	public void retrieveMentionsTrigger() {
		if (responseFlag == true) {
			LOGGER.info("<> Response triggered");
			List<Tweet> mentions = memeMentionsRetriever.retrieveMentions();
			mentions = utility.removeDuplicates(mentions);
			LOGGER.info("<> New mentions: mentions.size()={}", mentions.size());
			if (mentions != null && mentions.size() != 0) {
				memeResponder.processMentions(mentions);
			}
			responseFlag = false;
		}
	}
}
