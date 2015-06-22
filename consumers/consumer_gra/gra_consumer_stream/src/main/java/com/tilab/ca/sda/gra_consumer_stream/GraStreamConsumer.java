package com.tilab.ca.sda.gra_consumer_stream;

import com.sun.istack.logging.Logger;
import com.tilab.ca.sda.consumer.utils.BatchUtils;
import com.tilab.ca.sda.consumer.utils.stream.BusConsumerConnection;
import com.tilab.ca.sda.ctw.utils.RoundType;
import com.tilab.ca.sda.gra_consumer_batch.GraEvaluateAndCount;
import com.tilab.ca.sda.gra_consumer_batch.GraResultsMapping;
import com.tilab.ca.sda.gra_consumer_batch.utils.LoadUtils;
import com.tilab.ca.sda.gra_consumer_dao.GraConsumerDao;
import com.tilab.ca.sda.gra_core.GenderTypes;
import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.gra_core.StatsGenderCount;
import com.tilab.ca.sda.gra_core.components.GRA;
import com.tilab.ca.sda.sda.model.GeoStatus;
import com.tilab.ca.sda.sda.model.HtsStatus;
import com.tilab.ca.sda.sda.model.keys.DateHtKey;
import com.tilab.ca.sda.sda.model.keys.GeoLocTruncTimeKey;
import java.util.stream.Collectors;
import org.apache.spark.api.java.JavaRDD;
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
        final int roundMode=RoundType.fromString(graProps.defaultRoundMode());
        final int granMin=graProps.granMin();
        
        //save geo statuses
        countGeoStatuses(graProps.keyRaw(), busConnection, graProps, uniqueProfilesRDD, roundMode, granMin)
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
    
    }
    
    public static JavaPairDStream<Long,GenderTypes> getUniqueProfilesDStream(JavaDStream<String> rawTwDStream,GRA gra){
        return rawTwDStream.transformToPair((rawTwRdd) ->{
           JavaRDD<ProfileGender> distinctProfiles=GraEvaluateAndCount.evaluateUniqueProfilesRdd(rawTwRdd, gra);
           return GraEvaluateAndCount.fromProfileGenderToUserIdGenderPairRdd(distinctProfiles);
        });
    }
    
    
    public static JavaPairDStream<GeoLocTruncTimeKey, StatsGenderCount> countGeoStatuses(String rawKey,
            BusConsumerConnection busConsumerConnection,GraStreamProperties graProps,
        JavaPairDStream<Long,GenderTypes> uidGenders, int roundType, Integer granMin) {
        
        JavaDStream<String> rawTwDStream=busConsumerConnection.getDStreamByKey(rawKey);
        

        JavaPairDStream<Long,GeoStatus> uidGeoPair=rawTwDStream.mapToPair(rawTw -> {
            GeoStatus gs=BatchUtils.fromJstring2GeoStatus(rawTw, graProps.roundPos());
            return new Tuple2<Long,GeoStatus>(gs.getUserId(),gs);
        });
        
        JavaPairDStream<Long,GeoStatus> uidGeoPairWindow=uidGeoPair.window(new Duration(graProps.twTotWindowDurationMillis()), 
                                                                   new Duration(graProps.twTotWindowSlidingIntervalMillis()));
        
        return uidGeoPairWindow.join(uidGenders).transformToPair(geoRdd -> GraEvaluateAndCount.countGeoStatuses(geoRdd, roundType, granMin));
    }
    
    public static JavaPairDStream<DateHtKey, StatsGenderCount> countHtsStatuses(String rawKey,
            BusConsumerConnection busConsumerConnection,GraStreamProperties graProps,
        JavaPairDStream<Long,GenderTypes> uidGenders, int roundType, Integer granMin) {
        
        JavaDStream<String> rawTwDStream=busConsumerConnection.getDStreamByKey(rawKey);
        

        JavaPairDStream<Long,HtsStatus> uidHtPair=rawTwDStream.flatMapToPair(rawTw -> {
            return BatchUtils.fromJstring2HtsStatus(rawTw).stream().map(htStatus -> new Tuple2<Long,HtsStatus>(htStatus.getUserId(),htStatus))
                    .collect(Collectors.toList());
        });
        
        JavaPairDStream<Long,HtsStatus> uidHtPairWindow=uidHtPair.window(new Duration(graProps.twTotWindowDurationMillis()), 
                                                                   new Duration(graProps.twTotWindowSlidingIntervalMillis()));
        
        return uidHtPairWindow.join(uidGenders).transformToPair(htRdd -> GraEvaluateAndCount.countHtsStatuses(htRdd, roundType, granMin));
    }
}
