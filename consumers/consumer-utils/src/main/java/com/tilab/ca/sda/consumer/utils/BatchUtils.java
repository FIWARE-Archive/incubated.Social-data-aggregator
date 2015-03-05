package com.tilab.ca.sda.consumer.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tilab.ca.sda.ctw.utils.Utils;
import com.tilab.ca.sda.sda.model.GeoStatus;
import com.tilab.ca.sda.sda.model.HtsStatus;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


public class BatchUtils {

    private static final String GEOLOCATION_ELEM="geoLocation";
    private static final String HTS_ELEM="hashtagEntities";
    private static final String HT_TEXT_FIELD="text";
    private static final String RETWEET_STATUS_ELEM="retweetedStatus";
    private static final String REPLY_FIELD="inReplyToStatusId";
    private static final String CREATED_AT_FIELD="createdAt";
    private static final String USER_OBJ="user";
    private static final String ID_FIELD="id";
    private static final String LATITUDE_FIELD="latitude";
    private static final String LONGITUDE_FIELD="longitude";
   
    public static Optional<List<HtsStatus>> fromJstring2HtsStatus(String statusString) {
        Optional<List<HtsStatus>> htsStatusOptional=Optional.empty();
        JsonObject statusJsonObject = new JsonParser().parse(statusString).getAsJsonObject();
        if(statusContainsHashTags(statusJsonObject)){
            Date sentTime=Utils.Time.zonedDateTime2Date(Utils.Time.fromShortTimeZoneString2ZonedDateTime(
                    statusJsonObject.get(CREATED_AT_FIELD).getAsString()));
            long userId=statusJsonObject.getAsJsonObject(USER_OBJ).get(ID_FIELD).getAsLong();
            boolean isRetweet=isRetweet(statusJsonObject);
            boolean isReply=isReply(statusJsonObject);
            long postId=statusJsonObject.get(ID_FIELD).getAsLong();
            JsonArray htsArray=statusJsonObject.getAsJsonArray(HTS_ELEM);
            htsStatusOptional=Optional.of(getUniqueHtsFromHtsEntities(htsArray).stream()
                     .map((htStr) -> new HtsStatus(postId, userId, htStr, sentTime, isRetweet, isReply))
                     .collect(Collectors.toList()));
        }
        return htsStatusOptional;
    }
    
    
    public static Optional<GeoStatus> fromJstring2GeoStatus(String statusString,int geoDecTruncation) {
        
        Optional<GeoStatus> geoStatusOptional = Optional.empty();
        JsonObject statusJsonObject = new JsonParser().parse(statusString).getAsJsonObject();
        if (isGeoLocStatus(statusJsonObject)) {
            GeoStatus geoStatus = new GeoStatus();
            geoStatus.setSentTime(Utils.Time.zonedDateTime2Date(Utils.Time.fromShortTimeZoneString2ZonedDateTime(
                    statusJsonObject.get(CREATED_AT_FIELD).getAsString())));
            geoStatus.setUserId(statusJsonObject.getAsJsonObject(USER_OBJ).get(ID_FIELD).getAsLong());
            geoStatus.setRetweet(isRetweet(statusJsonObject));
            geoStatus.setReply(isReply(statusJsonObject));
            geoStatus.setPostId(statusJsonObject.get(ID_FIELD).getAsLong());
            JsonObject geoLocation=statusJsonObject.getAsJsonObject(GEOLOCATION_ELEM);
            geoStatus.setLatitude(geoLocation.get(LATITUDE_FIELD).getAsDouble());
            geoStatus.setLongitude(geoLocation.get(LONGITUDE_FIELD).getAsDouble());   
            geoStatus.setLatTrunc(Utils.truncateDouble(geoStatus.getLatitude(), geoDecTruncation));
            geoStatus.setLongTrunc(Utils.truncateDouble(geoStatus.getLongitude(), geoDecTruncation));
            geoStatusOptional=Optional.of(geoStatus);
        }
        return geoStatusOptional;
    }
    
    public static boolean statusContainsHashTags(JsonObject statusJsonObject) {
        return !statusJsonObject.getAsJsonArray(HTS_ELEM).isJsonNull() && statusJsonObject.getAsJsonArray(HTS_ELEM).size() >0;
    }
    
    public static Set<String> getUniqueHtsFromHtsEntities(JsonArray htEntities) {
        Set<String> htsSet=new HashSet<>();
        htEntities.forEach((jsonHt) -> htsSet.add(jsonHt.getAsJsonObject().get(HT_TEXT_FIELD).getAsString().toLowerCase()));
        return htsSet;
    }
    
    public static boolean isGeoLocStatus(JsonObject statusJsonObject) {
        return statusJsonObject.get(GEOLOCATION_ELEM)!=null && !statusJsonObject.get(GEOLOCATION_ELEM).isJsonNull();
    }
    
    public static boolean isRetweet(JsonObject statusJsonObject) {
        return statusJsonObject.get(RETWEET_STATUS_ELEM)!=null && !statusJsonObject.get(RETWEET_STATUS_ELEM).isJsonNull();
    }
    
    public static boolean isReply(JsonObject statusJsonObject) {
        return (statusJsonObject.get(REPLY_FIELD).getAsInt() != -1);
    }
}
