package com.tilab.ca.sda.sda.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class TwStatus implements Serializable {

    private static final long serialVersionUID = 374298244136183117L;

    private long postId;
    private Double latitude = null;
    private Double longitude = null;
    private Double latTrunc = null;
    private Double longTrunc = null;
    private Date sentTime = null;
    private String text = null;
    private long userId;
    private String source = null;
    private boolean retweet = false;
    private boolean reply = false;
    private String lang = null;
    private Set<String> hts = null;
    
    private String originalPostText = null;
    private String originalPostDisplayName = null;
    private String originalPostScreenName = null;
    private String originalPostProfileImageURL = null;
    private long originalPostId;
    
    private int retweetCount;
    private int replyCount;
    

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatTrunc() {
        return latTrunc;
    }

    public void setLatTrunc(Double latTrunc) {
        this.latTrunc = latTrunc;
    }

    public Double getLongTrunc() {
        return longTrunc;
    }

    public void setLongTrunc(Double longTrunc) {
        this.longTrunc = longTrunc;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Set<String> getHts() {
        return hts;
    }

    public void setHts(Set<String> hts) {
        this.hts = hts;
    }

    public String getOriginalPostText() {
        return originalPostText;
    }

    public void setOriginalPostText(String originalPostText) {
        this.originalPostText = originalPostText;
    }

    public String getOriginalPostDisplayName() {
        return originalPostDisplayName;
    }

    public void setOriginalPostDisplayName(String originalPostDisplayName) {
        this.originalPostDisplayName = originalPostDisplayName;
    }

    public String getOriginalPostScreenName() {
        return originalPostScreenName;
    }

    public void setOriginalPostScreenName(String originalPostScreenName) {
        this.originalPostScreenName = originalPostScreenName;
    }

    public String getOriginalPostProfileImageURL() {
        return originalPostProfileImageURL;
    }

    public void setOriginalPostProfileImageURL(String originalPostProfileImageURL) {
        this.originalPostProfileImageURL = originalPostProfileImageURL;
    }

    public long getOriginalPostId() {
        return originalPostId;
    }

    public void setOriginalPostId(long originalPostId) {
        this.originalPostId = originalPostId;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

}
