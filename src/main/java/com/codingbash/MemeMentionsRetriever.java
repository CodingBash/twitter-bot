package com.codingbash;

import java.util.List;

import org.springframework.social.twitter.api.Tweet;

public interface MemeMentionsRetriever {

	public List<Tweet> retrieveMentions();
}
