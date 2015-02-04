package com.tilab.ca.sda.sda.model;

import java.io.Serializable;
import java.util.Date;


public class HtsStatus implements Serializable{

	private static final long serialVersionUID = -5495418359564180448L;

	private String hashTag=null;
	private long postId;
	private long userId;
	private Date sentTime=null;
	private boolean retweet;
	private boolean reply;
	
	
	public HtsStatus(long postId,long userId,String hashTag,Date sentTime,boolean isRetweet,boolean isReply){
		this.postId=postId;
		this.hashTag=hashTag;
		this.sentTime=sentTime;
		this.userId=userId;
		this.retweet=isRetweet;
		this.reply=isReply;
	}
	
	public HtsStatus(){}

	public String getHashTag() {
		return hashTag;
	}

	public void setHashTag(String hashTag) {
		this.hashTag = hashTag;
	}

	public long getPostId() {
		return postId;
	}

	public void setPostId(long postId) {
		this.postId = postId;
	}
	
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Date getSentTime() {
		return sentTime;
	}

	public void setSentTime(Date sentTime) {
		this.sentTime = sentTime;
	}
	
	public boolean isRetweet() {
		return retweet;
	}

	public void setRetweet(boolean retweet) {
		this.retweet = retweet;
	}

	public boolean isReply() {
		return reply;
	}
	
	public void setReply(boolean reply) {
		this.reply = reply;
	}
	
	
}
