package com.tilab.ca.sda.gra_consumer_stream;

import com.sun.istack.logging.Logger;
import com.tilab.ca.sda.consumer.utils.stream.BusConsumerConnection;
import com.tilab.ca.sda.ctw.utils.RoundType;
import com.tilab.ca.sda.gra_consumer_batch.GraEvaluateAndCount;
import com.tilab.ca.sda.gra_consumer_batch.utils.LoadUtils;
import com.tilab.ca.sda.gra_consumer_dao.GraConsumerDao;
import com.tilab.ca.sda.gra_core.GenderTypes;
import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.gra_core.StatsGenderCount;
import com.tilab.ca.sda.gra_core.components.GRA;
import com.tilab.ca.sda.sda.model.GeoStatus;
import com.tilab.ca.sda.sda.model.keys.GeoLocTruncTimeKey;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;


public class GraStreamConsumer {
    
    private static final Logger log=Logger.getLogger(GraStreamConsumer.class);
    
    public static void start(JavaStreamingContext jssc,GraStreamProperties graProps,
                            GraConsumerDao graDao,BusConsumerConnection busConnection,String confsPath) throws Exception{
        busConnection.init(jssc);
        int roundMode=RoundType.fromString(graProps.defaultRoundMode());
        
        //create the gra configuration setting all the sub algorithms implementations and configuration parameters
        GRA.GRAConfig graConf=new GRA.GRAConfig().coloursClassifierModel(LoadUtils.loadColourClassifierModel(confsPath, graProps.coloursModelImplClass()))
                                              .descrClassifierModel(LoadUtils.loadDescrClassifierModel(confsPath, graProps.descrModelImplClass()))
                                              .featureExtractor(LoadUtils.loadDescrFeatureExtraction(confsPath, graProps.featureExtractionClassImpl()))
                                              .namesGenderMap(LoadUtils.loadNamesGenderMap(confsPath, graProps.namesGenderMapImplClass()))
                                              .trainingPath(graProps.trainingFilesPath())
                                              .numColorBitsMapping(graProps.colorAlgoReductionNumBits())
                                              .numColorBitsMapping(graProps.colorAlgoNumColorsToConsider());
       
        log.info("Creating gra instance..");
        GRA gra=new GRA(graConf, jssc.sparkContext());
        
        JavaDStream<String> rawTwDStream=busConnection.getDStreamByKey(graProps.keyRaw());
        JavaDStream<String> rawTwDStreamWindow=rawTwDStream.window(new Duration(graProps.twTotWindowDurationMillis()), 
                                                                   new Duration(graProps.twTotWindowSlidingIntervalMillis()));
        //retrieve distinct user profiles and evaluate their gender with gra algorithm
        JavaPairDStream<Long,GenderTypes> uniqueProfilesRDD=getUniqueProfilesDStream(rawTwDStreamWindow, gra);
        uniqueProfilesRDD.cache();
        
    
    }
    
    public static JavaPairDStream<Long,GenderTypes> getUniqueProfilesDStream(JavaDStream<String> rawTwDStream,GRA gra){
        return rawTwDStream.transformToPair((rawTwRdd) ->{
           JavaRDD<ProfileGender> distinctProfiles=GraEvaluateAndCount.evaluateUniqueProfilesRdd(rawTwRdd, gra);
           return GraEvaluateAndCount.fromProfileGenderToUserIdGenderPairRdd(distinctProfiles);
        });
    }
    
    
    public static JavaPairDStream<GeoLocTruncTimeKey, StatsGenderCount> countGeoStatuses(String rawKey,BusConsumerConnection busConsumerConnection,
        JavaPairDStream<Long,GenderTypes> uidGenders, int roundType, Integer granMin) {
        
        
        return null;
    }
}
