package com.codingbash.repository;

import org.springframework.social.twitter.api.TwitterProfile;

import com.codingbash.constant.SubscriptionResult;
import com.codingbash.model.MemeAccount;

public interface MemeRepository {

	public MemeAccount registerNewUser(MemeAccount memeAccount);

	public SubscriptionResult registerSubscriptionStatus(TwitterProfile twitterProfile, boolean willBeSubscribed);
}
