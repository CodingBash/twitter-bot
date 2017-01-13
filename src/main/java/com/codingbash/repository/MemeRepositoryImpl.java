package com.codingbash.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.stereotype.Repository;

import com.codingbash.constant.SubscriptionResult;
import com.codingbash.model.MemeAccount;

@Repository
public class MemeRepositoryImpl implements MemeRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemeRepositoryImpl.class);

	@Autowired
	private MemeAccountMongoRepository memeAccountMongoRepository;

	@Override
	public SubscriptionResult registerSubscriptionStatus(TwitterProfile twitterProfile, boolean willBeSubscribed) {
		LOGGER.info(
				"<> #registerSubscriptionStatus() - Begin subscription registration: twitterProfile.getId()={}, twitterProfile.getScreenName()={}, willBeSubscribed={}",
				twitterProfile.getId(), twitterProfile.getScreenName(), willBeSubscribed);
		// See if account already registered in MongoDB
		MemeAccount account = memeAccountMongoRepository.findByTwitterId(String.valueOf(twitterProfile.getId()));

		/*
		 * If account is not in Mongo (new user)
		 * 
		 * -Create a new account with a true subscription status
		 */
		if (account == null) {
			LOGGER.info(
					"< #registerSubscriptionStatus() - Account not found, will register new account: twitterProfile.getId()={}",
					twitterProfile.getId());

			account = new MemeAccount();
			account.setPoints(0);
			account.setSubscribed(willBeSubscribed);
			account.setTwitterId(String.valueOf(twitterProfile.getId()));
			account.setUsername(twitterProfile.getScreenName());

			account = registerNewUser(account);

			/*
			 * Will prevent NPE
			 */
			if (account == null) {

				LOGGER.warn("<> #registerSubscriptionStatus() - Unable to create account: twitterProfile.getId={}",
						twitterProfile.getId());
				return SubscriptionResult.NEW_ACCOUNT_REGISTRATION_ERROR;
			}
			LOGGER.info(
					" > #registerSubscriptionStatus() - New account registered: account.getTwitterId()={}, account.getMongoId()={}",
					account.getTwitterId(), account.getMongoId());
		}
		/*
		 * If account is already in Mongo
		 * 
		 */
		else {
			LOGGER.info(
					"<> #registerSubscriptionStatus() - Account already registered: account.getTwitterId(), account.getMongoId()={}, account.isSubscribed()={}",
					account.getTwitterId(), account.getMongoId(), account.isSubscribed());
			if (account.isSubscribed() == willBeSubscribed) {
				LOGGER.info(
						"<> #registerSubscriptionStatus() - Account already updated: account.getTwitterId()={}, account.getMongoId()",
						account.getTwitterId(), account.getMongoId());
				return SubscriptionResult.NO_ACTION_NECESSARY;
			} else {
				LOGGER.info(
						"<> #registerSubscriptionStatus() - Account not updated, now updating: account.getTwitterId()={}, account.getMongoId()={}",
						account.getTwitterId(), account.getMongoId());
				account.setSubscribed(willBeSubscribed);
				account = memeAccountMongoRepository.save(account);
				if (account == null) {
					LOGGER.warn("<> #registerSubscriptionStatus() - Unable to update account: twitterProfile.getId={}",
							twitterProfile.getId());
					return SubscriptionResult.SUBSCRIPTION_ACTION_REGISTRATION_ERROR;
				}
			}
		}
		return SubscriptionResult.SUBSCRIPTION_ACTION_SUCCESS;
	}

	@Override
	public MemeAccount registerNewUser(MemeAccount memeAccount) {
		LOGGER.info("< #createSubscriptionConfirmResponse() - Saving new user in Mongo: newAccount.getTwitterId()={}",
				memeAccount.getTwitterId());

		memeAccount = memeAccountMongoRepository.save(memeAccount);

		LOGGER.info(
				"< #createSubscriptionConfirmResponse() - Saved new user in Mongo: account.getTwitterId()={}, account.getMongoId()={}",
				memeAccount.getTwitterId(), memeAccount.getMongoId());

		return memeAccount;
	}
}
