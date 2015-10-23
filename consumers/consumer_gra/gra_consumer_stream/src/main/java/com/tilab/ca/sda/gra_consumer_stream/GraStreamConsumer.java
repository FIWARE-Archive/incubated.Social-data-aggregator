package com.tilab.ca.sda.gra_consumer_stream;

import com.sun.istack.logging.Logger;
import com.tilab.ca.sda.consumer.utils.BatchUtils;
import com.tilab.ca.sda.consumer.utils.stream.BusConsumerConnection;
import com.tilab.ca.sda.ctw.utils.RoundType;
import com.tilab.ca.sda.ctw.utils.Utils;
import com.tilab.ca.sda.gra_consumer_batch.GraEvaluateAndCount;
import com.tilab.ca.sda.gra_consumer_batch.GraResultsMapping;
import com.tilab.ca.sda.gra_consumer_batch.utils.LoadUtils;
import com.tilab.ca.sda.gra_consumer_dao.GraConsumerDao;
import com.tilab.ca.sda.gra_core.GenderTypes;
import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.gra_core.StatsGenderCount;
import com.tilab.ca.sda.gra_core.components.GRA;
import com.tilab.ca.sda.gra_core.components.GRAConfig;
import com.tilab.ca.sda.sda.model.GeoStatus;
import com.tilab.ca.sda.sda.model.HtsStatus;
import com.tilab.ca.sda.sda.model.keys.DateHtKey;
import com.tilab.ca.sda.sda.model.keys.GeoLocTruncTimeKey;
import java.util.stream.Collectors;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;


public class GraStreamConsumer {
    
    private static final Logger log=Logger.getLogger(GraStreamConsumer.class);
    
    public static void start(JavaStreamingContext jssc,GraStreamProperties graProps,
                            GraConsumerDao graDao,BusConsumerConnection busConnection,String confsPath) throws Exception{
        //init the bus connection
        busConnection.init(jssc);
        
        //create the gra configuration setting all the sub algorithms implementations and configuration parameters
        GRAConfig graConf=new GRAConfig().coloursClassifierModel(LoadUtils.loadColourClassifierModel(confsPath, graProps.coloursModelImplClass()))
                                              .descrClassifierModel(LoadUtils.loadDescrClassifierModel(confsPath, graProps.descrModelImplClass()))
                                              .featureExtractor(LoadUtils.loadDescrFeatureExtraction(confsPath, graProps.featureExtractionClassImpl()))
                                              .namesGenderMap(LoadUtils.loadNamesGenderMap(confsPath, graProps.namesGenderMapImplClass()))
                                              .trainingPath(graProps.trainingFilesPath())
                                              .numColorBitsMapping(graProps.colorAlgoReductionNumBits())
                                              .numColorsMapping(graProps.colorAlgoNumColorsToConsider());
       
        log.info("Creating gra instance..");
        GRA gra=Utils.Load.getClassInstFromInterface(GRA.class, graProps.graClassImpl());
        gra.init(graConf, jssc.sparkContext());
        
        
        JavaDStream<String> rawTwDStream=busConnection.getDStreamByKey(graProps.keyRaw());
        JavaDStream<String> rawTwDStreamWindow=rawTwDStream.window(new Duration(graProps.graWindowDurationMillis()), 
                                                                   new Duration(graProps.graWindowSlidingIntervalMillis()));
        
        //retrieve distinct user profiles and evaluate their gender with gra algorithm
        JavaDStream<ProfileGender> uniqueProfilesGenderDStream=getUniqueProfilesDStream(rawTwDStreamWindow, gra); //jssc.sparkContext()
        JavaPairDStream<Long,GenderTypes> uidGenderPairsDStream=getUidGenderPairDStream(uniqueProfilesGenderDStream);
        
        uidGenderPairsDStream.cache();
        final int roundMode=RoundType.fromString(graProps.defaultRoundMode());
        final int granMin=graProps.granMin();
        
        
        //save profiles
        uniqueProfilesGenderDStream.foreachRDD( profilePairRDD -> {
                Logger.getLogger(GraStreamConsumer.class).info(String.format("saving %d distinct profiles on storage..",profilePairRDD.count()));
                graDao.saveTwGenderProfiles(
                        profilePairRDD.map(profile -> GraResultsMapping.fromProfileGender2TwGenderProfile(profile))
                );
                return null;
        });
        
        //save geo statuses
        countGeoStatuses(rawTwDStreamWindow, uidGenderPairsDStream,graProps.roundPos(), roundMode, granMin)
                .foreachRDD(geoPairRdd ->{
                    Logger.getLogger(GraStreamConsumer.class).info(String.format("saving %d geo objects on storage..",geoPairRdd.count()));
                    graDao.saveGeoByTimeGran(
                            geoPairRdd.map(geoPairStatus -> 
                                    GraResultsMapping.fromStatsGenderCountToStatsPreGenderGeo(geoPairStatus._1,
                                                                                              geoPairStatus._2,roundMode,granMin)
                            ));
                    return null;
                });
        //save hts statuses
        countHtsStatuses(rawTwDStreamWindow,uidGenderPairsDStream, roundMode, granMin)
                .foreachRDD(htPairRdd -> {
                    Logger.getLogger(GraStreamConsumer.class).info(String.format("saving %d ht objects on storage..",htPairRdd.count()));
                    graDao.saveHtsByTimeGran(
                            htPairRdd.map(htPairStatus ->
                                    GraResultsMapping.fromStatsGenderCountToStatsPreGenderHt(htPairStatus._1, 
                                                                                             htPairStatus._2, roundMode, granMin)
                            )
                    );
                    return null;
                });
    }
    
    public static JavaDStream<ProfileGender> getUniqueProfilesDStream(JavaDStream<String> rawTwDStream,GRA gra){ //,JavaSparkContext jsc
        return rawTwDStream.transform(rawTwRdd -> GraEvaluateAndCount.evaluateUniqueProfilesRdd(rawTwRdd, gra)); //jsc
    }
    
    public static JavaPairDStream<Long,GenderTypes> getUidGenderPairDStream(JavaDStream<ProfileGender> distinctProfiles){
        return distinctProfiles.transformToPair(GraEvaluateAndCount::fromProfileGenderToUserIdGenderPairRdd);
    }
    
    
    public static JavaPairDStream<GeoLocTruncTimeKey, StatsGenderCount> countGeoStatuses(JavaDStream<String> rawTwDStreamWindow,
        JavaPairDStream<Long,GenderTypes> uidGenders,int roundPos, int roundType, Integer granMin) {
        
        JavaPairDStream<Long,GeoStatus> uidGeoPair=rawTwDStreamWindow.mapToPair(rawTw -> {
            GeoStatus gs=BatchUtils.fromJstring2GeoStatus(rawTw, roundPos);
            return new Tuple2<Long,GeoStatus>(gs.getUserId(),gs);
        });
        
        return uidGeoPair.join(uidGenders).transformToPair(geoRdd -> GraEvaluateAndCount.countGeoStatuses(geoRdd, roundType, granMin));
    }
    
    public static JavaPairDStream<DateHtKey, StatsGenderCount> countHtsStatuses(JavaDStream<String> rawTwDStreamWindow,
            JavaPairDStream<Long,GenderTypes> uidGenders, int roundType, Integer granMin) {
        
        JavaPairDStream<Long,HtsStatus> uidHtPair=rawTwDStreamWindow.flatMapToPair(rawTw -> {
            return BatchUtils.fromJstring2HtsStatus(rawTw).stream().map(htStatus -> new Tuple2<Long,HtsStatus>(htStatus.getUserId(),htStatus))
                    .collect(Collectors.toList());
        });
        
        return uidHtPair.join(uidGenders).transformToPair(htRdd -> GraEvaluateAndCount.countHtsStatuses(htRdd, roundType, granMin));
    }
}
