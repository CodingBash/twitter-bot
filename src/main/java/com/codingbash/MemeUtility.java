package com.codingbash;

import static com.codingbash.constant.MemeConstants.MAX_WAIT_RESPONSE_TIME_IN_MS;
import static com.codingbash.constant.MemeConstants.MEME_ACCOUNTS;
import static com.codingbash.constant.MemeConstants.MEME_ARCHIVE_SIZE_LIMIT;
import static com.codingbash.constant.MemeConstants.RATE_LIMIT_CUSHION_AMOUNT;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.social.twitter.api.RateLimitStatus;
import org.springframework.social.twitter.api.ResourceFamily;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;

import com.codingbash.model.TweetDataPayload;

@Component
public class MemeUtility {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemeUtility.class);

	@Autowired
	private Twitter twitter;

	@Autowired
	private Queue<TweetDataPayload> postTweetQueue;

	@Autowired
	@Qualifier("memeArchive")
	private List<Tweet> memeArchive;

	@Autowired
	@Qualifier("homeTweets")
	private List<Tweet> homeTweets;

	public void checkRateLimit(ResourceFamily resourceFamily, String endpoint) {
		List<RateLimitStatus> rateLimitStatusList = twitter.userOperations().getRateLimitStatus(resourceFamily)
				.get(resourceFamily);
		for (RateLimitStatus status : rateLimitStatusList) {
			if (status.getEndpoint().equals(endpoint)) {
				LOGGER.info("<> Checking rate limit: status.getEndpoint={}, status.getRemainingHits={}",
						status.getEndpoint(), status.getRemainingHits());
				if (status.getRemainingHits() <= RATE_LIMIT_CUSHION_AMOUNT) {
					LOGGER.warn("<> RATE LIMIT MET, SLEEPING FOR {} SECONDS", status.getSecondsUntilReset());
					try {
						Thread.sleep(status.getSecondsUntilReset() * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public synchronized void reloadTheMemes() {
		LOGGER.info("< #reloadTheMemes(): current memeArchive.size()={}", memeArchive.size());
		List<String> memeAccounts = new ArrayList<String>();
		for (String memeAccount : MEME_ACCOUNTS) {
			memeAccounts.add(memeAccount);
		}
		final int MEME_ACCOUNT_SIZE_LIMIT = MEME_ARCHIVE_SIZE_LIMIT / memeAccounts.size();
		memeArchive.clear();
		for (String memeAccount : memeAccounts) {
			checkRateLimit(ResourceFamily.STATUSES, "/statuses/user_timeline");
			memeArchive.addAll(twitter.timelineOperations().getUserTimeline(memeAccount, MEME_ACCOUNT_SIZE_LIMIT));
		}
		LOGGER.info("> #reloadTheMemes(): memeArchive.size()={}", memeArchive.size());
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

			/*
			 * If mention is already handled (duplicate)
			 */
			homeIteration: for (Tweet homeTweet : homeTweets) {
				if(homeTweet == null){
					/* TODO: HANDLE HOMETWEET FOR NULLS (Since development sender returns a null)
					 * -Should I have a development version of removeDuplicates?
					 * --Go with this implmentation first, and keep a todo in
					 */
					continue homeIteration;
				}
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

			/*
			 * If mention does not begin with "@AskMemebot" TODO: Dynamically
			 * generate username
			 */
			if (!mention.getText().trim().substring(0, "@AskMemebot".length()).equalsIgnoreCase("@AskMemebot")) {
				LOGGER.info("<> Non meme request detected - EXCLUDING MENTION: mention.getId()={}", mention.getIdStr());
				continue mentionIteration;
			}

			/*
			 * If mention is not a reply to a tweet TODO: Specify that it is not
			 * a reply to an @AskMemebot tweet
			 */
			if (mention.getInReplyToStatusId() != null) {
				LOGGER.info("<> Mention is a reply - EXCLUDING MENTION: mention.getId()={}", mention.getIdStr());
				continue mentionIteration;
			}
			sanitizedMentions.add(mention);
		}

		LOGGER.info("> #removeDuplicates(): sanitizedMentions.size()={}", sanitizedMentions.size());
		return sanitizedMentions;
	}

	/*
	 * TODO: Determine to make synchronous
	 */
	public void addPayloadToQueue(TweetDataPayload payload) {
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
