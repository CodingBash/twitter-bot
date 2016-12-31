package com.codingbash.constant;

public class MemeConstants {

	// TODO: Retrieve from database/cache
	public static final String[] CUSTOM_MESSAGE_ARRAY = { "Here's your meme {} lol:", "Your meme is ready {}:",
			"Created with love {} <3:", "This will make your day {}:", "Thy meme hast arriv'd {}:",
			"A meme just for u, {}:", "Hi {}, this meme is 4 u:", "I picked this for u, {}:", "Tell ur friends {}:",
			"Hey {}, this dank meme is for you:", "Bots love memes too {}:", "I bet you can relate to this one {} lol:" };

	// TODO: Retrieve from database/cache
	public static final String[] MEME_ACCOUNTS = { "memearchive", "FreeMemesKids", "themostdank", "DankMemePlug",
			"memeprovider", "DailyMemeSupply" };

	// TODO: Retrieve from database/cache
	public static final int MEME_ARCHIVE_SIZE_LIMIT = 300;

	// TODO: Put in application.yaml
	public static final int POST_TWEET_INTERVAL_AMOUNT = 20;

	// TODO: Put in application.yaml
	public static final int POST_TWEET_INTERVAL_TIME_IN_MS = 900000;

	// TODO: Put in application.yaml
	public static final int MAX_WAIT_RESPONSE_TIME_IN_MS = 30000;

	// TODO: Put in application.yaml
	public static final int RATE_LIMIT_CUSHION_AMOUNT = 5;
}
