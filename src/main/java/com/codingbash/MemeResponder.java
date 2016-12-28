package com.codingbash;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.TweetData;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;

@Component
public class MemeResponder {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemeMentionsRetriever.class);

	@Autowired
	private Twitter twitter;

	private List<Tweet> homeTweets = new ArrayList<Tweet>();
	// TODO: Externalize
	// TODO: Space must be included before and after message. Move space
	// addition to tweet creation.
	private static final String[] customMessageArray = { " Here's your meme lol. ", " Your meme is ready. ",
			" Memes lol ", " This will make your day ", " " };

	@Async
	public void replyToMentions(List<Tweet> tweets, List<Tweet> memeArchive) {
		LOGGER.info("Replying to mentions");
		if (homeTweets.size() == 0) {
			homeTweets.addAll(twitter.timelineOperations().getUserTimeline(200));
		}

		outerloop: for (Tweet tweet : tweets) {
			for (Tweet homeTweet : homeTweets) {
				// TODO: TEST
				if (tweet.getId() == homeTweet.getInReplyToStatusId()) {
					LOGGER.info("Duplicate response detected: " + tweet.getIdStr());
					// TODO: Don't like how I break, make a new method
					break outerloop;
				}
			}
			String username = "@" + tweet.getFromUser();
			int randomMemeIndex = (int) (Math.random() * ((double) memeArchive.size()));
			int randomCustomMessageIndex = (int) (Math.random() * ((double) customMessageArray.length));
			Tweet memeTweet = memeArchive.get(randomMemeIndex);
			String memeLink = String.format("https://twitter.com/%s/status/%s", memeTweet.getFromUser(),
					memeTweet.getIdStr());
			TweetData tweetData = new TweetData(username + customMessageArray[randomCustomMessageIndex] + memeLink);
			tweetData.inReplyToStatus(tweet.getId());
			twitter.timelineOperations().updateStatus(tweetData);
		}
	}
}
