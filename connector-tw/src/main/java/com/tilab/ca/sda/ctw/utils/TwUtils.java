package com.tilab.ca.sda.ctw.utils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
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
    
    	if(html.indexOf('>')==-1){
    		return null;
    	}
    	else{
    		source=html.substring((html.indexOf('>')+1), html.lastIndexOf('<'));
    	}
    	
		return source;	
    }
}
