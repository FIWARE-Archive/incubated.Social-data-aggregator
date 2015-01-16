package com.tilab.ca.sda.ctw.data;

import com.tilab.ca.sda.ctw.utils.TwUtils;
import com.tilab.ca.sda.ctw.utils.Utils;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import twitter4j.GeoLocation;
import twitter4j.Status;

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

    public String getOriginalPostText() {
        return originalPostText;
    }

    public void setOriginalPostText(String originalPostText) {
        this.originalPostText = originalPostText;
    }

    private String originalPostText = null;
    private String originalPostDisplayName = null;
    private String originalPostScreenName = null;
    private String originalPostProfileImageURL = null;
    private long originalPostId;

    public long getOriginalPostId() {
        return originalPostId;
    }

    public void setOriginalPostId(long originalPostId) {
        this.originalPostId = originalPostId;
    }

    public Set<String> getHts() {
        return hts;
    }

    public void setHts(Set<String> hts) {
        this.hts = hts;
    }
    private int retweetCount;
    private int replyCount;

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
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

    public static TwStatus twStatusFromStatus(Status status) {
        TwStatus tws = new TwStatus();
        tws.setLang(status.getLang());
        tws.setPostId(status.getId());
        tws.setReply(TwUtils.isReply(status));
        tws.setRetweet(TwUtils.isRetweet(status));
        tws.setRetweetCount(0);
        tws.setReplyCount(0);
        GeoLocation gl = status.getGeoLocation();

        if (gl != null) {
            tws.setLatitude(gl.getLatitude());
            tws.setLongitude(gl.getLongitude());
            tws.setLatTrunc(Utils.truncateDouble(gl.getLatitude(), 3));
            tws.setLongTrunc(Utils.truncateDouble(gl.getLongitude(), 3));
        }

        tws.setSentTime(status.getCreatedAt());
        tws.setSource(TwUtils.getSourceFromHTML(status.getSource()));
        tws.setText(status.getText());
        tws.setUserId(status.getUser().getId());

        tws.setHts(TwUtils.getUniqueHtsFromHtsEntities(status.getHashtagEntities()));

        if (status.isRetweet() && status.getRetweetedStatus() != null) {
            tws.setOriginalPostId(status.getRetweetedStatus().getId());
            tws.setOriginalPostDisplayName(status.getRetweetedStatus().getUser().getName());
            tws.setOriginalPostScreenName(status.getRetweetedStatus().getUser().getScreenName());
            tws.setOriginalPostProfileImageURL(status.getRetweetedStatus().getUser().getProfileImageURL());
            tws.setOriginalPostText(status.getRetweetedStatus().getText());
        }

        return tws;
    }

}
