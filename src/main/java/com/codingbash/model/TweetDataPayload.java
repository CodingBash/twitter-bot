package com.codingbash.model;

import java.util.List;

import org.springframework.core.io.Resource;

public class TweetDataPayload {
	private String message;
	private Long inReplyToStatusId;
	private List<Resource> resourceMediaList;

	public synchronized String getMessage() {
		return message;
	}

	public synchronized void setMessage(String message) {
		int difference = 0;
		if (message.contains("https://t.co/")) {
			String newMessage = message.replaceAll("https://t.co/.[a-zA-Z0-9_.-]*", "");
			difference = message.length() - newMessage.length();
			message = newMessage;
		}
		if (message.length() + difference > 140) {
			message = message.substring(0, 140 - difference);
		}
		this.message = message;
	}

	public synchronized Long getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	public synchronized void setInReplyToStatusId(Long inReplyToStatusId) {
		this.inReplyToStatusId = inReplyToStatusId;
	}

	public synchronized List<Resource> getResourceMediaList() {
		return resourceMediaList;
	}

	public synchronized void setResourceMediaList(List<Resource> resourceMediaList) {
		this.resourceMediaList = resourceMediaList;
	}

}
