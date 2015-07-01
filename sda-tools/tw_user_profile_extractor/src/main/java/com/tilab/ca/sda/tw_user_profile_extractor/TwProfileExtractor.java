package com.tilab.ca.sda.tw_user_profile_extractor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping.TwProfile;
import com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping.TwProfileEvaluated;
import com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping.TwRawProfile;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;


public class TwProfileExtractor {
    
    private static final String USER_OBJ="user";
    private static final char GENDER_UNKNOWN='u';
    
    public static JavaRDD<TwProfile> extractUsrProfiles(final JavaSparkContext sc,final String inputDataPath,final boolean testSetProfiles) {
        JavaRDD<String> rawTweets=sc.textFile(inputDataPath);
        
        return rawTweets.mapToPair((twStatusJStr) -> {
           TwProfile tp=mapRawJson2TwProfile(twStatusJStr, testSetProfiles);
           return new Tuple2<Long,TwProfile>(tp.getUserId(),tp);
         }) //take distinct users getting the latest updated user profile
         .reduceByKey((tuple2Profile1,tuple2Profile2) -> tuple2Profile1.getPostId()>tuple2Profile2.getPostId()?tuple2Profile1:tuple2Profile2)
         .map((tuple2TwProfile) ->tuple2TwProfile._2);
    }
    
    public static TwProfile mapRawJson2TwProfile(String rawJson,boolean testSetProfiles){
        TwProfile twProfile=testSetProfiles?new TwRawProfile():new TwProfileEvaluated();
        JsonObject statusJsonObject = new JsonParser().parse(rawJson).getAsJsonObject();
        
        twProfile.setPostId(statusJsonObject.get("id").getAsLong());
        
        JsonObject userObj=statusJsonObject.getAsJsonObject(USER_OBJ);
        twProfile.setUserId(userObj.get("id").getAsLong());
        
        if(userObj.get("description")!=null && !userObj.get("description").isJsonNull())
            twProfile.setDescription(userObj.get("description").getAsString());
        
        twProfile.setName(userObj.get("name").getAsString());
        twProfile.setScreenName(userObj.get("screenName").getAsString());
        twProfile.setProfileBackgroundColor(userObj.get("profileBackgroundColor").getAsString());
        twProfile.setProfileTextColor(userObj.get("profileTextColor").getAsString());
        twProfile.setProfileLinkColor(userObj.get("profileLinkColor").getAsString());
        twProfile.setProfileSidebarFillColor(userObj.get("profileSidebarFillColor").getAsString());
        twProfile.setProfileSidebarBorderColor(userObj.get("profileSidebarBorderColor").getAsString());
        
        if(userObj.get("url")!=null && !userObj.get("url").isJsonNull())
            twProfile.setUrl(userObj.get("url").getAsString());
        
        if(userObj.get("location")!=null && !userObj.get("location").isJsonNull())
            twProfile.setLocation(userObj.get("location").getAsString());
        
        
        twProfile.setGender(GENDER_UNKNOWN);
        
        if(testSetProfiles)
            ((TwRawProfile)twProfile).setDataType(TwRawProfile.TRAINING_TYPE);
        
        return twProfile;
    }
}
