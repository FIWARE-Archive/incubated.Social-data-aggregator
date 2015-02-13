package com.tilab.ca.sda.ctw.utils;

import com.tilab.ca.sda.sda.model.GeoStatus;
import com.tilab.ca.sda.sda.model.HtsStatus;
import com.tilab.ca.sda.sda.model.TwStatus;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.Status;

public class TwUtils {
    
    

    public static boolean isReply(Status status) {
        return (status.getInReplyToStatusId() != -1);
    }

    public static boolean isRetweet(Status status) {
        return (status.getRetweetedStatus() != null);
    }

    public static boolean isGeoLocStatus(Status status) {
        return status.getGeoLocation() != null;
    }

    public static boolean statusContainsHashTags(Status status) {
        return status.getHashtagEntities() != null && status.getHashtagEntities().length > 0;
    }
    
    public static Set<String> getUniqueHtsFromHtsEntities(HashtagEntity[] htEntities){
		return Arrays.asList(htEntities).stream()
			.map((htEntity) -> htEntity.getText().toLowerCase()).collect(Collectors.toSet());
    }
    
    public static String getSourceFromHTML(String html){ 	
    	String source=null;
    
    	if(html==null || html.indexOf('>')==-1){
    		return html;
    	}
    	else{
    		source=html.substring((html.indexOf('>')+1), html.lastIndexOf('<'));
    	}
    	
		return source;	
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
    
    public static List<HtsStatus> htsStatusesFromStatus(Status status){
		if(!TwUtils.statusContainsHashTags(status))
			return null;
		List<HtsStatus> htsStatusList=new LinkedList<>();
		
		TwUtils.getUniqueHtsFromHtsEntities(status.getHashtagEntities()).forEach(
				(ht) -> {
					htsStatusList.add(new HtsStatus(status.getId(),status.getUser().getId(),
								ht,status.getCreatedAt(),TwUtils.isRetweet(status),TwUtils.isReply(status)));
				}
		);
		
		return htsStatusList;
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
