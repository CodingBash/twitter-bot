package com.codingbash;

import static com.codingbash.constant.MemeConstants.MEME_ACCOUNTS;
import static com.codingbash.constant.MemeConstants.MEME_ARCHIVE_SIZE_LIMIT;
import static com.codingbash.constant.MemeConstants.RATE_LIMIT_CUSHION_AMOUNT;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.social.twitter.api.RateLimitStatus;
import org.springframework.social.twitter.api.ResourceFamily;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;

@Component
public class MemeUtility {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemeUtility.class);

	@Autowired
	private Twitter twitter;

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
			LOGGER.info("<> Checking for duplicates");
			homeIteration: for (Tweet homeTweet : homeTweets) {
				if (homeTweet != null) {
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
			}
			sanitizedMentions.add(mention);
		}

		LOGGER.info("> #removeDuplicates(): sanitizedMentions.size()={}", sanitizedMentions.size());
		return sanitizedMentions;
	}
}
