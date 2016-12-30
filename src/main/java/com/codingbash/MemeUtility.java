package com.codingbash;

import static com.codingbash.constant.MemeConstants.RATE_LIMIT_CUSHION_AMOUNT;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.RateLimitStatus;
import org.springframework.social.twitter.api.ResourceFamily;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;

@Component
public class MemeUtility {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemeUtility.class);

	@Autowired
	private Twitter twitter;

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
}
