package com.codingbash.scheduler;

import static com.codingbash.constant.MemeConstants.MEME_ACCOUNTS;
import static com.codingbash.constant.MemeConstants.MEME_ARCHIVE_SIZE_LIMIT;
import static com.codingbash.constant.MemeConstants.POST_TWEET_INTERVAL_TIME_IN_MS;
import static com.codingbash.constant.MemeConstants.MAX_WAIT_RESPONSE_TIME_IN_MS;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;

import com.codingbash.model.PostTweetLimiter;
import com.codingbash.model.TweetDataPayload;
import com.codingbash.responder.MemeResponder;
import com.codingbash.responder.MemeSender;
import com.codingbash.retriever.MemeMentionsRetriever;

/**
 * Schedules a trigger to retrieve all recent mentions and initiate reply
 * 
 * @author CodingBash
 *
 */
@Component
@Profile({ "development", "production" })
public class MemeTriggerSchedulerImpl implements MemeTriggerScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemeTriggerSchedulerImpl.class);

	@Autowired
	private Twitter twitter;

	@Autowired
	private MemeMentionsRetriever memeMentionsRetriever;

	@Autowired
	private MemeResponder memeResponder;

	@Autowired
	private Queue<TweetDataPayload> postTweetQueue;

	@Autowired
	private MemeSender memeSender;

	@Autowired
	private PostTweetLimiter limiter;

	@Autowired
	@Qualifier("homeTweets")
	private List<Tweet> homeTweets;

	private List<Tweet> memeArchive = new ArrayList<Tweet>();

	private boolean responseFlag = false;

	// TODO: Rate limit avoidance
	@Override
	@Scheduled(fixedDelay = MAX_WAIT_RESPONSE_TIME_IN_MS)
	public void responseTrigger() {
		if (responseFlag == true) {
			LOGGER.info("<> Response triggered");
			List<Tweet> mentions = memeMentionsRetriever.retrieveMentions();
			LOGGER.info("<> New mentions: mentions.size()={} " + mentions.size());

			if (mentions != null && mentions.size() != 0) {
				while (memeArchive.size() == 0) {
					reloadTheMemes();
				}
				memeResponder.replyToMentions(mentions, memeArchive);
			}
			responseFlag = false;
		}
	}

	@Override
	@Scheduled(cron = "0 0 3 * * ?")
	public void memeReloadTrigger() {
		LOGGER.info("<> Meme reload triggered");
		reloadTheMemes();
	}

	@Scheduled(fixedDelay = 1000)
	public void sendResponse() {
		TweetDataPayload payload = postTweetQueue.peek();
		if (payload != null) {
			if (limiter.permit()) {
				homeTweets.add(memeSender.sendTweet(payload));
				postTweetQueue.remove();
			}
		} else {
			responseFlag = true;
		}

	}

	@Scheduled(fixedRate = POST_TWEET_INTERVAL_TIME_IN_MS)
	public void refreshCounter() {
		limiter.refresh();
	}

	@Override
	public synchronized void reloadTheMemes() {
		LOGGER.info("< #reloadTheMemes(): current memeArchive.size()={}", memeArchive.size());
		List<String> memeAccounts = new ArrayList<String>();
		for (String memeAccount : MEME_ACCOUNTS) {
			memeAccounts.add(memeAccount);
		}
		final int MEME_ACCOUNT_SIZE_LIMIT = MEME_ARCHIVE_SIZE_LIMIT / memeAccounts.size();
		memeArchive.clear();
		for (String memeAccount : memeAccounts) {
			memeArchive.addAll(twitter.timelineOperations().getUserTimeline(memeAccount, MEME_ACCOUNT_SIZE_LIMIT));
		}
		LOGGER.info("> #reloadTheMemes(): memeArchive.size()={}", memeArchive.size());
	}

}
