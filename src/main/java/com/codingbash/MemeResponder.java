package com.codingbash;

import java.util.List;

import org.springframework.social.twitter.api.Tweet;

public interface MemeResponder {

	void replyToMentions(List<Tweet> tweets, List<Tweet> memeArchive);
	
}

