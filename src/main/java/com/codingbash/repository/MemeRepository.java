package com.codingbash.repository;

import org.springframework.social.twitter.api.TwitterProfile;

import com.codingbash.constant.SubscriptionResult;
import com.codingbash.model.MemeAccount;

public interface MemeRepository {

	public SubscriptionResult registerSubscriptionStatus(TwitterProfile twitterProfile);

	public MemeAccount registerNewUser(MemeAccount memeAccount);
}
