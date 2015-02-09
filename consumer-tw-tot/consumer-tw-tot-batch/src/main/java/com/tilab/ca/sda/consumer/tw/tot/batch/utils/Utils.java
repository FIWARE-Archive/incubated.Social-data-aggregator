package com.tilab.ca.sda.consumer.tw.tot.batch.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tilab.ca.sda.sda.model.GeoStatus;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


public class Utils {

    private static final String GEOLOCATION_ELEM="geoLocation";
    private static final String HTS_ELEM="hashtagEntities";
    private static final String HT_TEXT_FIELD="text";
    private static final String RETWEET_STATUS_ELEM="retweetedStatus";
    private static final String REPLY_FIELD="inReplyToStatusId";
    
   
    
    public static boolean statusContainsHashTags(JsonObject statusJsonObject) {
        return !statusJsonObject.getAsJsonArray(HTS_ELEM).isJsonNull() && statusJsonObject.getAsJsonArray(HTS_ELEM).size() >0;
    }
    
    public static Set<String> getUniqueHtsFromHtsEntities(JsonArray htEntities) {
        Set<String> htsSet=new HashSet<>();
        htEntities.forEach((jsonHt) -> htsSet.add(jsonHt.getAsJsonObject().get(HT_TEXT_FIELD).getAsString().toLowerCase()));
        return htsSet;
    }
    
    public static boolean isGeoLocStatus(JsonObject statusJsonObject) {
        return statusJsonObject.get(GEOLOCATION_ELEM) != null;
    }
    
    public static boolean isRetweet(JsonObject statusJsonObject) {
        return statusJsonObject.get(RETWEET_STATUS_ELEM) != null;
    }
    
    public static boolean isReply(JsonObject statusJsonObject) {
        return (statusJsonObject.get(REPLY_FIELD).getAsInt() != -1);
    }
    
    public static Optional<GeoStatus> fromJstring2GeoStatus(String statusString){
        
        JsonObject statusJsonObject=new JsonParser().parse(statusString).getAsJsonObject();
        Optional<GeoStatus> geoStatus=Optional.empty();
        
        
        
        return null;
    }
}
