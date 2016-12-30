package com.codingbash.model;

import static com.codingbash.constant.MemeConstants.POST_TWEET_INTERVAL_AMOUNT;
import java.util.concurrent.atomic.AtomicInteger;

public class PostTweetLimiter {

	private AtomicInteger counter;

	public PostTweetLimiter() {
		counter = new AtomicInteger();
	}

	public synchronized boolean permit() throws IllegalStateException {
		if (counter.get() <= 0) {
			return false;
		} else {
			counter.decrementAndGet();
			return true;
		}
	}

	public synchronized void refresh() {
		counter.set(POST_TWEET_INTERVAL_AMOUNT);
	}

	public synchronized int get() {
		return counter.get();
	}
}
