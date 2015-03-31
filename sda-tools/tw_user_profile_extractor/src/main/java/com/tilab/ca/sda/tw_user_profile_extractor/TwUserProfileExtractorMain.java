package com.tilab.ca.sda.tw_user_profile_extractor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tilab.ca.sda.ctw.utils.Utils;
import com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping.TwProfile;
import com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping.TwProfileEvaluated;
import com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping.TwRawProfile;
import com.tilab.ca.sda.tw_user_profile_extractor.utils.Arguments;
import com.tilab.ca.sda.tw_user_profile_extractor.utils.CommandLineArgs;
import java.io.File;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class TwUserProfileExtractorMain {

    private static final Logger log = Logger.getLogger(TwUserProfileExtractorMain.class);
    
    private static final String USER_OBJ="user";
    private static final char GENDER_UNKNOWN='u';

    public static void main(String args[]) {
        try {
            String confsPath=Utils.Env.getConfsPathFromEnv(ProfileExtractorConsts.SDA_CONF_SYSTEM_PROPERTY, ProfileExtractorConsts.PE_SYSTEM_PROPERTY);
            String log4jPropsFilePath=confsPath+File.separator+ProfileExtractorConsts.LOG4jPROPS_FILE_NAME;
            PropertyConfigurator.configure(log4jPropsFilePath);
            
            log.info("Start Tw Profile Extractor Tool");
            log.info("Parsing commandline arguments...");
            
            Arguments arguments=CommandLineArgs.parseCommandLineArgs(args);
            
            SparkConf conf=new SparkConf().setAppName(ProfileExtractorConsts.APP_NAME);
	    JavaSparkContext sc=new JavaSparkContext(conf);
            JavaRDD<TwProfile> twProfiles=extractUsrProfiles(sc,arguments.getInputDataPath(),arguments.areTrainingData());
            
        } catch (Exception ex) {
            log.error(ex);
        }
    }

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
        twProfile.setDescription(userObj.get("description").getAsString());
        twProfile.setName(userObj.get("name").getAsString());
        twProfile.setScreenName(userObj.get("screenName").getAsString());
        twProfile.setProfileBackgroundColor(userObj.get("profileBackgroundColor").getAsString());
        twProfile.setProfileTextColor(userObj.get("profileTextColor").getAsString());
        twProfile.setLocation(userObj.get("location").getAsString());
        twProfile.setGender(GENDER_UNKNOWN);
        
        if(testSetProfiles)
            ((TwRawProfile)twProfile).setDataType(TwRawProfile.TRAINING_TYPE);
        
        return twProfile;
    }

    
}
