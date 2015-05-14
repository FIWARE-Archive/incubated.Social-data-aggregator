package com.tilab.ca.sda.consumer.tw.tot.stream;

import com.tilab.ca.sda.consumer.tw.tot.core.TotTwConstants;
import com.tilab.ca.sda.consumer.tw.tot.core.TwCounter;
import com.tilab.ca.sda.consumer.tw.tot.core.data.DateHtKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.GeoLocTruncTimeKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.StatsCounter;
import com.tilab.ca.sda.consumer.tw.tot.dao.ConsumerTwTotDao;
import com.tilab.ca.sda.consumer.utils.stream.BusConsumerConnection;
import com.tilab.ca.sda.ctw.utils.JsonUtils;
import com.tilab.ca.sda.ctw.utils.RoundType;
import com.tilab.ca.sda.sda.model.GeoStatus;
import com.tilab.ca.sda.sda.model.HtsStatus;
import org.apache.log4j.Logger;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;


public class TotTwStreamConsumer {
    
    private static final Logger log=Logger.getLogger(TotTwStreamConsumer.class);
   
    /**
     * 
     * @param jssc
     * @param twDao
     * @param twProps
     * @param busConnection 
     */
    public static void executeAnalysis(JavaStreamingContext jssc,ConsumerTwTotDao twDao,TwTotConsumerProperties twProps,BusConsumerConnection busConnection){
        busConnection.init(jssc);
        int roundMode=RoundType.fromString(twProps.defaultRoundMode());
        JavaPairDStream<GeoLocTruncTimeKey, StatsCounter> geoStatusPairDstream=collectGeoStatus(twProps, busConnection, roundMode);
        log.info(String.format("[%s]saving %d geo statuses on storage..", TotTwConstants.TOT_TW_CONSUMER_LOG_TAG,geoStatusPairDstream.count()));
        geoStatusPairDstream.foreachRDD((geoStatusRDD) -> {
            twDao.saveGeoByTimeGran(geoStatusRDD);
            return null;
        });
        JavaPairDStream<DateHtKey, StatsCounter> htStatusPairDstream=collectHtsStatus(twProps, busConnection, roundMode);
        log.info(String.format("[%s]saving %d hts statuses on storage..", TotTwConstants.TOT_TW_CONSUMER_LOG_TAG,htStatusPairDstream.count()));
        htStatusPairDstream.foreachRDD((htsStatusRDD) -> {
            twDao.saveHtsByTimeGran(htsStatusRDD);
            return null;
        });
    }
    
    /**
     * Count geo statuses using geoLocation and time as keys 
     * @param twProps
     * @param busConnection
     * @param roundMode
     * @return 
     */
    public static JavaPairDStream<GeoLocTruncTimeKey, StatsCounter> collectGeoStatus(TwTotConsumerProperties twProps,BusConsumerConnection busConnection,final int roundMode){
         String geoKey = twProps.keyGeo();
         log.info(String.format("[%s]get all the geo from the bus..", TotTwConstants.TOT_TW_CONSUMER_LOG_TAG));
         JavaDStream<GeoStatus> geoStatuses =busConnection.getDStreamByKey(geoKey).map((geoStatusJsonStr) -> JsonUtils.deserialize(geoStatusJsonStr, GeoStatus.class));
         JavaDStream<GeoStatus> geoStatusWindow =geoStatuses.window(new Duration(twProps.twTotWindowDurationMillis()), new Duration(twProps.twTotWindowSlidingIntervalMillis()));
         return geoStatusWindow.transformToPair((geoRDD) -> TwCounter.countGeoStatuses(geoRDD,roundMode, twProps.granMin()));     
    }
    
    /**
     * 
     * @param twProps
     * @param busConnection
     * @param roundMode
     * @return 
     */
    public static JavaPairDStream<DateHtKey, StatsCounter> collectHtsStatus(TwTotConsumerProperties twProps,BusConsumerConnection busConnection,final int roundMode){
         String htKey = twProps.keyHt();
         log.info(String.format("[%s]get all the hts from the bus..", TotTwConstants.TOT_TW_CONSUMER_LOG_TAG));
         JavaDStream<HtsStatus> htsStatuses =busConnection.getDStreamByKey(htKey).map((htStatusJsonStr) -> JsonUtils.deserialize(htStatusJsonStr, HtsStatus.class));
         JavaDStream<HtsStatus> htsStatusWindow =htsStatuses.window(new Duration(twProps.twTotWindowDurationMillis()), new Duration(twProps.twTotWindowSlidingIntervalMillis()));
         return htsStatusWindow.transformToPair((htsRDD) -> TwCounter.countHtsStatuses(htsRDD,roundMode, twProps.granMin()));     
    }
    
}
