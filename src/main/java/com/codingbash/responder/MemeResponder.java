package com.codingbash.responder;

import java.util.List;

import org.springframework.social.twitter.api.Tweet;

public interface MemeResponder {

	public void replyToMentions(List<Tweet> tweets, List<Tweet> memeArchive);

}
