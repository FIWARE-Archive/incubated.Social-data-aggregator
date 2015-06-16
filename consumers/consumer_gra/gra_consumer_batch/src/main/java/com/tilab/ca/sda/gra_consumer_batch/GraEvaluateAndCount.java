package com.tilab.ca.sda.gra_consumer_batch;

import com.tilab.ca.sda.consumer.utils.BatchUtils;
import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.gra_core.components.GRA;
import com.tilab.ca.sda.sda.model.TwUserProfile;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;


public class GraEvaluateAndCount {
    
    public static JavaRDD<ProfileGender> evaluateUniqueProfilesRdd(JavaRDD<String> tweetsRdd,GRA gra){
        
        JavaRDD<TwUserProfile> uniqueProfilesRdd = tweetsRdd.map(BatchUtils::fromJstring2TwUserProfile)
                         .mapToPair(twUserProfile -> new Tuple2<Long,TwUserProfile>(twUserProfile.getUid(),twUserProfile))
                         .reduceByKey((pr1,pr2) -> pr1.getLastUpdate().isAfter(pr2.getLastUpdate())?pr1:pr2) //get the most recent profile
                         .map(tuple2UserProfile -> tuple2UserProfile._2);
        
        return gra.waterfallGraEvaluation(uniqueProfilesRdd);
    }
    
    
    
}
