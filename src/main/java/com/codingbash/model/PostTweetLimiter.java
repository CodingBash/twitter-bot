package com.codingbash.model;

import static com.codingbash.constant.MemeConstants.POST_TWEET_INTERVAL_AMOUNT;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostTweetLimiter {

	private static final Logger LOGGER = LoggerFactory.getLogger(PostTweetLimiter.class);

	private AtomicInteger counter;

	public PostTweetLimiter() {
		counter = new AtomicInteger();
	}

	public synchronized boolean permit() throws IllegalStateException {
		if (counter.get() <= 0) {
			LOGGER.info("<> #permit() - NOT permitting: counter.get()={}", counter.get());
			return false;
		} else {
			LOGGER.info("<> #permit() - permitting: pre-counter.get()={}", counter.get());
			counter.decrementAndGet();
			return true;
		}
	}

	public synchronized void refresh() {
		LOGGER.info("< #refresh() - refreshing");
		counter.set(POST_TWEET_INTERVAL_AMOUNT);
		LOGGER.info("> #refresh(): counter.get={}", counter.get());
	}

	public synchronized int get() {
		return counter.get();
	}
}
