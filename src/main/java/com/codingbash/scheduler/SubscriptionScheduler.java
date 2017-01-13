package com.codingbash.scheduler;

import java.util.List;

import static com.codingbash.constant.MemeConstants.SUBSCRIPTION_SCHEDULER_CRON_JOB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.codingbash.model.MemeAccount;
import com.codingbash.repository.MemeAccountMongoRepository;
import com.codingbash.responder.MemeResponder;

@Component
public class SubscriptionScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionScheduler.class);
	@Autowired
	private MemeAccountMongoRepository memeAccountMongoRepository;

	@Autowired
	MongoTemplate template;

	@Autowired
	private MemeResponder memeResponder;

	// @Scheduled(cron = SUBSCRIPTION_SCHEDULER_CRON_JOB)
	@Scheduled(fixedRate = 3600000)
	public void sendSubscribedMemesTrigger() {
		LOGGER.info("< #sendSubscribedMemesTrigger() - Subscription trigger initiated");
		List<MemeAccount> subscribedMemeAccounts = memeAccountMongoRepository.findBySubscribed(true);
		LOGGER.info("<> #sendSubscribedMemesTrigger() - Retrieved subscribed meme accounts: subscribedMemeAccounts={}",
				subscribedMemeAccounts.size());

		for (MemeAccount memeAccount : subscribedMemeAccounts) {
			memeResponder.createMemeResponse(memeAccount.getUsername(), null);
		}

		LOGGER.info("> #sendSubscribedMemesTrigger() - All subscription tweets sent to queue");
	}
}
