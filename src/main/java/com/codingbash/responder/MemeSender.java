package com.codingbash.responder;

import java.util.Queue;

import org.springframework.social.twitter.api.Tweet;

import com.codingbash.model.TweetDataPayload;

public interface MemeSender {

	public Tweet sendTweet(TweetDataPayload payload);

}
