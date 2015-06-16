package com.tilab.ca.sda.consumer.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tilab.ca.sda.ctw.utils.Utils;
import com.tilab.ca.sda.sda.model.GeoStatus;
import com.tilab.ca.sda.sda.model.HtsStatus;
import com.tilab.ca.sda.sda.model.TwUserProfile;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;


public class BatchUtils {
    
    private static final Logger log=Logger.getLogger(BatchUtils.class);

    private static final String USER_ELEM="user";
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
    
    private static final String USER_NAME_FIELD="name";
    private static final String USER_SCREEN_NAME_FIELD="screenName";
    private static final String USER_DESCRIPTION_FIELD="description";
    private static final String USER_PROFILE_BACKGROUND_COLOR_FIELD="profileBackgroundColor";
    private static final String USER_PROFILE_TEXT_COLOR_FIELD="profileTextColor";
    private static final String USER_PROFILE_LINK_COLOR_FIELD="profileLinkColor";
    private static final String USER_PROFILE_SIDEBAR_FILL_COLOR_FIELD="profileSidebarFillColor";
    private static final String USER_PROFILE_SIDEBAR_BORDER_COLOR_FIELD="profileSidebarBorderColor";
    
    private static final String JHTS_FORMAT=String.format("\"%s\":[",HTS_ELEM);
    private static final String JHTS_FORMAT_EMPTY=String.format("\"%s\":[]",HTS_ELEM);
    private static final String JGEO_FORMAT=String.format("\"%s\":{",GEOLOCATION_ELEM);
    private static final String JGEO_FORMAT_EMPTY=String.format("\"%s\":{}",GEOLOCATION_ELEM);
   
    public static List<HtsStatus> fromJstring2HtsStatus(String statusString) {
        log.debug("parsing status string "+statusString);
        List<HtsStatus> htsStatusList=new LinkedList<>();
        try{
            JsonObject statusJsonObject = new JsonParser().parse(statusString).getAsJsonObject();
            if(statusContainsHashTags(statusJsonObject)){
                Date sentTime=Utils.Time.zonedDateTime2Date(Utils.Time.fromShortTimeZoneString2ZonedDateTime(
                        statusJsonObject.get(CREATED_AT_FIELD).getAsString()));
                long userId=statusJsonObject.getAsJsonObject(USER_OBJ).get(ID_FIELD).getAsLong();
                boolean isRetweet=isRetweet(statusJsonObject);
                boolean isReply=isReply(statusJsonObject);
                long postId=statusJsonObject.get(ID_FIELD).getAsLong();
                JsonArray htsArray=statusJsonObject.getAsJsonArray(HTS_ELEM);
                htsStatusList=getUniqueHtsFromHtsEntities(htsArray).stream()
                         .map((htStr) -> new HtsStatus(postId, userId, htStr, sentTime, isRetweet, isReply))
                         .collect(Collectors.toList());
            }
        }catch(Exception e){
          log.error("failed to parse or error while processing jstring "+statusString,e);
        }
        return htsStatusList;
    }
    
    
    public static GeoStatus fromJstring2GeoStatus(String statusString,int geoDecTruncation) {
        log.debug("geoStatus: parsing status string "+statusString);
        GeoStatus  geoStatus = new GeoStatus();
        try{
            JsonObject statusJsonObject = new JsonParser().parse(statusString).getAsJsonObject();
            if (isGeoLocStatus(statusJsonObject)) {
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
            }
        }catch(Exception e){
          log.error("failed to parse or error while processing jstring "+statusString,e);
        }
        return geoStatus;
    }
    
    public static TwUserProfile fromJstring2TwUserProfile(String statusString) {
        
        TwUserProfile tup=new TwUserProfile();
        try{
            JsonObject statusJsonObject = new JsonParser().parse(statusString).getAsJsonObject();
            JsonObject jUser=statusJsonObject.getAsJsonObject(USER_ELEM);
            tup.setUid(jUser.get(ID_FIELD).getAsLong());
            tup.setName(jUser.get(USER_NAME_FIELD).getAsString());
            tup.setScreenName(jUser.get(USER_SCREEN_NAME_FIELD).getAsString());
            tup.setDescription(jUser.get(USER_DESCRIPTION_FIELD).getAsString());
            String[] colors=new String[5];
            colors[0]=jUser.get(USER_PROFILE_BACKGROUND_COLOR_FIELD).getAsString();
            colors[1]=jUser.get(USER_PROFILE_TEXT_COLOR_FIELD).getAsString();
            colors[2]=jUser.get(USER_PROFILE_LINK_COLOR_FIELD).getAsString();
            colors[3]=jUser.get(USER_PROFILE_SIDEBAR_FILL_COLOR_FIELD).getAsString();
            colors[4]=jUser.get(USER_PROFILE_SIDEBAR_BORDER_COLOR_FIELD).getAsString();
            tup.setProfileColors(colors);
            tup.setLastUpdate(Utils.Time.fromShortTimeZoneString2ZonedDateTime(statusJsonObject.get(CREATED_AT_FIELD).getAsString()));
        }catch(Exception e){
          log.error("failed to parse or error while processing jstring "+statusString,e);
        }
        return tup;
    }
    
    public static boolean statusContainsHashTags(JsonObject statusJsonObject) {
        log.info("calling statusContainsHashTags");
        return statusJsonObject.getAsJsonArray(HTS_ELEM)!=null && !statusJsonObject.getAsJsonArray(HTS_ELEM).isJsonNull() && statusJsonObject.getAsJsonArray(HTS_ELEM).size() >0;
    }
    
    public static boolean isHtsStatus(String jStatus){
        return jStatus.contains(JHTS_FORMAT) && !jStatus.contains(JHTS_FORMAT_EMPTY);
    }
    
    public static Set<String> getUniqueHtsFromHtsEntities(JsonArray htEntities) {
        log.debug("calling getUniqueHtsFromHtsEntities");
        Set<String> htsSet=new HashSet<>();
        htEntities.forEach((jsonHt) -> htsSet.add(jsonHt.getAsJsonObject().get(HT_TEXT_FIELD).getAsString().toLowerCase()));
        return htsSet;
    }
    
    public static boolean isGeoLocStatus(JsonObject statusJsonObject) {
        log.debug("calling isGeoLocStatus");
        return statusJsonObject.get(GEOLOCATION_ELEM)!=null && !statusJsonObject.get(GEOLOCATION_ELEM).isJsonNull();
    }
    
    public static boolean isGeoLocStatus(String jStatus) {
        return jStatus.contains(JGEO_FORMAT) && !jStatus.contains(JGEO_FORMAT_EMPTY);
    }
    
    public static boolean isRetweet(JsonObject statusJsonObject) {
        return statusJsonObject.get(RETWEET_STATUS_ELEM)!=null && !statusJsonObject.get(RETWEET_STATUS_ELEM).isJsonNull();
    }
    
    public static boolean isReply(JsonObject statusJsonObject) {
        return (statusJsonObject.get(REPLY_FIELD).getAsInt() != -1);
    }
}
