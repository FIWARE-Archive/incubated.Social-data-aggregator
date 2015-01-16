package com.tilab.ca.sda.ctw.data;

import com.tilab.ca.sda.ctw.utils.TwUtils;
import com.tilab.ca.sda.ctw.utils.Utils;
import java.io.Serializable;
import java.util.Date;
import twitter4j.GeoLocation;
import twitter4j.Status;


public class GeoStatus implements Serializable{

	private static final long serialVersionUID = -3727945179954945660L;

	private long postId;
	private long userId;
	private double latitude;
	private double longitude;
	private double latTrunc;
	private double longTrunc;
	private Date sentTime=null;
	private boolean retweet;
	private boolean reply;
	
	
	
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

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatTrunc() {
		return latTrunc;
	}

	public void setLatTrunc(double latTrunc) {
		this.latTrunc = latTrunc;
	}

	public double getLongTrunc() {
		return longTrunc;
	}

	public void setLongTrunc(double longTrunc) {
		this.longTrunc = longTrunc;
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
	
	public static GeoStatus geoStatusFromStatus(Status status){
		if(!TwUtils.isGeoLocStatus(status))
			return null;
		
		GeoLocation gl=status.getGeoLocation();
		GeoStatus gs=new GeoStatus();
		gs.setLatitude(gl.getLatitude());
		gs.setLongitude(gl.getLongitude());
		gs.setPostId(status.getId());
		gs.setUserId(status.getUser().getId());
		gs.setRetweet(TwUtils.isRetweet(status));
		gs.setReply(TwUtils.isReply(status));
		gs.setSentTime(status.getCreatedAt());
		gs.setLatTrunc(Utils.truncateDouble(gl.getLatitude(), 3));
		gs.setLongTrunc(Utils.truncateDouble(gl.getLongitude(), 3));
		
		return gs;
	}
	
}
