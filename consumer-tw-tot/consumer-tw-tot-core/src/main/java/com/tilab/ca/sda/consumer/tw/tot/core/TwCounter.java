package com.tilab.ca.sda.consumer.tw.tot.core;

import com.tilab.ca.sda.consumer.tw.tot.core.data.DateHtKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.GeoLocTruncKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.GeoLocTruncTimeKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.StatsCounter;
import com.tilab.ca.sda.ctw.utils.RoundManager;
import com.tilab.ca.sda.ctw.utils.Utils;
import com.tilab.ca.sda.sda.model.GeoStatus;
import com.tilab.ca.sda.sda.model.HtsStatus;
import java.time.ZonedDateTime;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;

public class TwCounter {
    
    /**
     * Count the number of geoStatus grouped by time (rounder following the criteria passed as parameters) and an area (latitude truncated and longitude truncated)
     * @param geoStatuses geoStatuses to evaluate
     * @param roundType round type check RoundType class to see the allowed
     * round types
     * @param granMin the granularity expressed in minutes (needed only for RoundType.ROUND_TYPE_MIN)
     * @param from
     * @param to
     * @return a JavaPairRDD containing the tweet count by time and area
     */
    public static JavaPairRDD<GeoLocTruncTimeKey, StatsCounter> countGeoStatuses(JavaRDD<GeoStatus> geoStatuses,int roundType,Integer granMin,ZonedDateTime from,ZonedDateTime to){
        return geoStatuses
                .filter((geoStatus) -> Utils.Time.isBetween(from, to, Utils.Time.date2ZonedDateTime(geoStatus.getSentTime()), Utils.Time.EXTREME_INCLUDED))
                .mapToPair((geoStatus)-> 
                        new Tuple2<GeoLocTruncTimeKey, StatsCounter>(
                            new GeoLocTruncTimeKey(RoundManager.roundDate(geoStatus.getSentTime(), roundType, granMin), 
                                               geoStatus.getLatTrunc(), geoStatus.getLongTrunc()),
                            new StatsCounter(geoStatus))
                ).reduceByKey((statGeoCounter1, statGeoCounter2) -> statGeoCounter1.sum(statGeoCounter2)); 
    }
    
    /**
     * Count the number of geoStatus grouped by area (latitude truncated and longitude truncated) in a time interval passed as parameter
     * @param geoStatuses
     * @param from
     * @param to
     * @return 
     */
    public static JavaPairRDD<GeoLocTruncKey, StatsCounter> countGeoStatusesFromTimeBounds(JavaRDD<GeoStatus> geoStatuses,ZonedDateTime from,ZonedDateTime to){
        return geoStatuses
                .filter((geoStatus) -> Utils.Time.isBetween(from, to, Utils.Time.date2ZonedDateTime(geoStatus.getSentTime()), Utils.Time.EXTREME_INCLUDED))
                .mapToPair((geoStatus)-> 
                        new Tuple2<GeoLocTruncKey, StatsCounter>(
                            new GeoLocTruncKey(geoStatus.getLatTrunc(), geoStatus.getLongTrunc()),
                            new StatsCounter(geoStatus))
                ).reduceByKey((statGeoCounter1, statGeoCounter2) -> statGeoCounter1.sum(statGeoCounter2)); 
    }
    
    /**
     * Count the number of htsStatus grouped by time (rounder following the criteria passed as parameters) and a hashTag
     * @param htsStatuses
     * @param roundType
     * @param granMin
     * @param from
     * @param to
     * @return 
     */
    public static JavaPairRDD<DateHtKey, StatsCounter> countHtsStatuses(JavaRDD<HtsStatus> htsStatuses,int roundType,Integer granMin,ZonedDateTime from,ZonedDateTime to){
        return htsStatuses
                .filter((geoStatus) -> Utils.Time.isBetween(from, to, Utils.Time.date2ZonedDateTime(geoStatus.getSentTime()), Utils.Time.EXTREME_INCLUDED))
                .mapToPair((htStatus)-> 
                        new Tuple2<DateHtKey, StatsCounter>(new DateHtKey(RoundManager.roundDate(htStatus.getSentTime(), roundType, granMin), 
                                                            htStatus.getHashTag()),
                                                            new StatsCounter(htStatus))
                ).reduceByKey((htCounter1, htCounter2) -> htCounter1.sum(htCounter2)); 
    }
    
    /**
     * Count the number of htsStatus grouped by HashTag in a time interval passed as parameter
     * @param htsStatuses
     * @param from
     * @param to
     * @return 
     */
    public static JavaPairRDD<String, StatsCounter> countHtsStatusesFromTimeBounds(JavaRDD<HtsStatus> htsStatuses,ZonedDateTime from,ZonedDateTime to){
        return htsStatuses
                .filter((htStatus) -> Utils.Time.isBetween(from, to, Utils.Time.date2ZonedDateTime(htStatus.getSentTime()), Utils.Time.EXTREME_INCLUDED))
                .mapToPair((htStatus)-> new Tuple2<String, StatsCounter>(htStatus.getHashTag(),new StatsCounter(htStatus)))
                .reduceByKey((htCounter1, htCounter2) -> htCounter1.sum(htCounter2)); 
    }
   
}

