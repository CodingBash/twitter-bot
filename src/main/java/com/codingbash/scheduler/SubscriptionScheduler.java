package com.codingbash.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.codingbash.model.MemeAccount;
import com.codingbash.repository.MemeAccountMongoRepository;

@Component
public class SubscriptionScheduler {

	@Autowired
	private MemeAccountMongoRepository memeAccountMongoRepository;

	//@Scheduled(fixedRate = 10000)
	public void sendSubscribedMemesTrigger() {
		List<MemeAccount> subscribedMemeAccounts = memeAccountMongoRepository.findBySubscribed(true);
		for (MemeAccount memeAccount : subscribedMemeAccounts) {
			System.out.println(memeAccount.getUsername());
		}
		// TODO: This should now use the same method that responseTrigger uses.
		// TODO: Send a meme to each person
		// There should be no issue with rate limit since all responses are sent
		// to a single queue
	}
}
