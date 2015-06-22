package com.tilab.ca.sda.gra_consumer_batch;

import com.tilab.ca.sda.consumer.utils.BatchUtils;
import com.tilab.ca.sda.ctw.utils.RoundManager;
import com.tilab.ca.sda.gra_core.GenderTypes;
import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.gra_core.StatsGenderCount;
import com.tilab.ca.sda.gra_core.components.GRA;
import com.tilab.ca.sda.sda.model.GeoStatus;
import com.tilab.ca.sda.sda.model.HtsStatus;
import com.tilab.ca.sda.sda.model.TwUserProfile;
import com.tilab.ca.sda.sda.model.keys.DateHtKey;
import com.tilab.ca.sda.sda.model.keys.GeoLocTruncKey;
import com.tilab.ca.sda.sda.model.keys.GeoLocTruncTimeKey;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;


public class GraEvaluateAndCount {
    
    private static final Logger log=Logger.getLogger(GraEvaluateAndCount.class);
    /**
     * 
     * @param tweetsRdd
     * @param gra
     * @return 
     */
    public static JavaRDD<ProfileGender> evaluateUniqueProfilesRdd(JavaRDD<String> tweetsRdd,GRA gra){
        log.debug("evaluating unique profiles RDD...");
        JavaRDD<TwUserProfile> uniqueProfilesRdd = tweetsRdd.map(BatchUtils::fromJstring2TwUserProfile)
                         .mapToPair(twUserProfile -> new Tuple2<Long,TwUserProfile>(twUserProfile.getUid(),twUserProfile))
                         .reduceByKey((pr1,pr2) -> pr1.getLastUpdate().isAfter(pr2.getLastUpdate())?pr1:pr2) //get the most recent profile
                         .map(tuple2UserProfile -> tuple2UserProfile._2);
        log.debug("got unique profiles RDD");
        return gra.waterfallGraEvaluation(uniqueProfilesRdd);
    }
    
    /**
     * 
     * @param profilesGenderRdd
     * @return 
     */
    public static JavaPairRDD<Long,GenderTypes> fromProfileGenderToUserIdGenderPairRdd(JavaRDD<ProfileGender> profilesGenderRdd){
        return profilesGenderRdd.mapToPair(profileGender -> new Tuple2<>(profileGender.getTwProfile().getUid(),profileGender.getGender()));
    }
    
    
   /**
    * 
    * @param geoStatuses
    * @param uidGenders
    * @param roundType
    * @param granMin
    * @return 
    */
    public static JavaPairRDD<GeoLocTruncTimeKey, StatsGenderCount> countGeoStatuses(JavaRDD<GeoStatus> geoStatuses,JavaPairRDD<Long,GenderTypes> uidGenders, int roundType, Integer granMin) {
        
        JavaPairRDD<Long,GeoStatus> geoStatusesUidPairRdd=geoStatuses.mapToPair(geoStatus -> new Tuple2<>(geoStatus.getUserId(),geoStatus));
        
        return countGeoStatuses(geoStatusesUidPairRdd.join(uidGenders), roundType, granMin);
    }
    
    
    public static JavaPairRDD<GeoLocTruncTimeKey, StatsGenderCount> countGeoStatuses(JavaPairRDD<Long,Tuple2<GeoStatus,GenderTypes>> joinedGeoGenderRdd,int roundType, Integer granMin){
       return joinedGeoGenderRdd.mapToPair(joinedGeoGender -> 
                                        new Tuple2<>(new GeoLocTruncTimeKey(RoundManager.roundDate(joinedGeoGender._2._1.getSentTime(), 
                                                                            roundType, granMin),joinedGeoGender._2._1.getLatTrunc(), 
                                                                            joinedGeoGender._2._1.getLongTrunc()),
                                        new StatsGenderCount(joinedGeoGender._2._2,joinedGeoGender._2._1.isRetweet(),
                                                                                        joinedGeoGender._2._1.isReply()))
                             )
                             .reduceByKey((statGeoCounter1, statGeoCounter2) -> statGeoCounter1.sum(statGeoCounter2));
    }
    
    /**
     * 
     * @param geoStatuses
     * @param uidGenders
     * @return 
     */
    public static JavaPairRDD<GeoLocTruncKey, StatsGenderCount> countGeoStatusesFromTimeBounds(JavaRDD<GeoStatus> geoStatuses,JavaPairRDD<Long,GenderTypes> uidGenders) {
        
        JavaPairRDD<Long,GeoStatus> geoStatusesUidPairRdd=geoStatuses.mapToPair(geoStatus -> new Tuple2<>(geoStatus.getUserId(),geoStatus));
        
        return geoStatusesUidPairRdd.join(uidGenders)
                .mapToPair((joinedGeoGender)
                        -> new Tuple2<GeoLocTruncKey, StatsGenderCount>(
                                new GeoLocTruncKey(joinedGeoGender._2._1.getLatTrunc(), joinedGeoGender._2._1.getLongTrunc()),
                                new StatsGenderCount(joinedGeoGender._2._2,joinedGeoGender._2._1.isRetweet(),
                                                                                        joinedGeoGender._2._1.isReply()))
                ).reduceByKey((statGeoCounter1, statGeoCounter2) -> statGeoCounter1.sum(statGeoCounter2));
    }
    
    /**
     * 
     * @param htsStatuses
     * @param uidGenders
     * @param roundType
     * @param granMin
     * @return 
     */
    public static JavaPairRDD<DateHtKey, StatsGenderCount> countHtsStatuses(JavaRDD<HtsStatus> htsStatuses,JavaPairRDD<Long,GenderTypes> uidGenders, int roundType, Integer granMin) {
        
        JavaPairRDD<Long,HtsStatus> htsStatusesUidPairRdd=htsStatuses.mapToPair(htsStatus -> new Tuple2<>(htsStatus.getUserId(),htsStatus));
        
        return countHtsStatuses(htsStatusesUidPairRdd.join(uidGenders),roundType,granMin);
                
    }
    
    public static JavaPairRDD<DateHtKey, StatsGenderCount> countHtsStatuses(JavaPairRDD<Long,Tuple2<HtsStatus,GenderTypes>> joinedHtGenderRdd,int roundType, Integer granMin) {
        
        return joinedHtGenderRdd
                .mapToPair(joinedHtGender -> new Tuple2<>(new DateHtKey(RoundManager.roundDate(joinedHtGender._2._1.getSentTime(), 
                                                                        roundType, granMin),joinedHtGender._2._1.getHashTag()),
                                                          new StatsGenderCount(joinedHtGender._2._2,joinedHtGender._2._1.isRetweet(),
                                                                                        joinedHtGender._2._1.isReply()))
                ).reduceByKey((htCounter1, htCounter2) -> htCounter1.sum(htCounter2));
    }

    /**
     * 
     * @param htsStatuses
     * @param uidGenders
     * @return 
     */
    public static JavaPairRDD<String, StatsGenderCount> countHtsStatusesFromTimeBounds(JavaRDD<HtsStatus> htsStatuses, JavaPairRDD<Long,GenderTypes> uidGenders) {
        JavaPairRDD<Long,HtsStatus> htsStatusesUidPairRdd=htsStatuses.mapToPair(htsStatus -> new Tuple2<>(htsStatus.getUserId(),htsStatus));
        
        return htsStatusesUidPairRdd.join(uidGenders)
                                    .mapToPair(joinedHtGender -> new Tuple2<>(joinedHtGender._2._1.getHashTag(),
                                                                              new StatsGenderCount(joinedHtGender._2._2,joinedHtGender._2._1.isRetweet(),
                                                                                        joinedHtGender._2._1.isReply()))
                                    ).reduceByKey((htCounter1, htCounter2) -> htCounter1.sum(htCounter2));
    }
    
    
}
