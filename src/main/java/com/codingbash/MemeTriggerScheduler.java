package com.codingbash;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;

/**
 * Schedules a trigger to retrieve all recent mentions and initiate reply
 * 
 * @author CodingBash
 *
 */
@Component
public class MemeTriggerScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemeTriggerScheduler.class);
	
	@Autowired
	private Twitter twitter;

	@Autowired
	private MemeMentionsRetriever memeMentionsRetriever;

	@Autowired
	private MemeResponder memeResponder;

	private List<Tweet> memeArchive = new ArrayList<Tweet>();

	// TODO: Remove these state variables
	private boolean reloadTheMemesFlag = false;

	// TODO: Rate limit avoidance
	@Scheduled(fixedDelay = 10000)
	public void responseTrigger() {
		LOGGER.info("Response triggered");
		List<Tweet> mentions = memeMentionsRetriever.retrieveMentions();
		LOGGER.info("New mentions: " + mentions.size());
		if (mentions != null && mentions.size() != 0) {
			while (reloadTheMemesFlag == true || memeArchive.size() == 0) {
				reloadTheMemes();
			}
			memeResponder.replyToMentions(mentions, memeArchive);
		}
	}

	// TODO: Change to cron scheduler!!!
	@Scheduled(fixedRate = 360000)
	public void memeReloadTrigger() {
		LOGGER.info("Meme reload triggered");
		reloadTheMemesFlag = true;
	}

	public void reloadTheMemes() {
		LOGGER.info("Memes reloading");
		// TODO: Store this externally (cache, .properties, etc)
		List<String> memeAccounts = new ArrayList<String>();
		memeAccounts.add("memearchive");
		memeAccounts.add("FreeMemesKids");
		memeAccounts.add("themostdank");
		memeAccounts.add("MemeSupplier");
		memeAccounts.add("DailyMemeSupply");
		for (String memeAccount : memeAccounts) {
			memeArchive.addAll(twitter.timelineOperations().getUserTimeline(memeAccount));
		}
		reloadTheMemesFlag = false;
	}

}
