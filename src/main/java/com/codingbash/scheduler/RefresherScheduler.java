package com.codingbash.scheduler;

import static com.codingbash.constant.MemeConstants.POST_TWEET_INTERVAL_TIME_IN_MS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.codingbash.MemeUtility;
import com.codingbash.model.PostTweetLimiter;

@Component
public class RefresherScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(RefresherScheduler.class);

	@Autowired
	private PostTweetLimiter limiter;

	@Autowired
	private MemeUtility utility;

	@Scheduled(fixedRate = POST_TWEET_INTERVAL_TIME_IN_MS)
	public void refreshCounterTrigger() {
		LOGGER.info("<> Refreshing the post limiter from a count of {}", limiter.get());
		limiter.refresh();
	}

	@Scheduled(cron = "0 0 3 * * ?") // Every 3AM
	public void memeReloadTrigger() {
		LOGGER.info("<> Meme reload triggered");
		utility.reloadTheMemes();
	}
}
