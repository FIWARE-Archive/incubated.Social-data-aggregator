package com.tilab.ca.sda.consumer.tw.tot.dao;

import com.tilab.ca.sda.consumer.tw.tot.core.data.DateHtKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.GeoLocTruncKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.GeoLocTruncTimeKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.StatsCounter;
import java.io.Serializable;
import java.util.Date;
import org.apache.spark.api.java.JavaPairRDD;


public interface ConsumerTwTotDao extends Serializable{
    
    public static final String CONF_PATH_PROPS_KEY="confPath";
    
    public void saveGeoByTimeGran(JavaPairRDD<GeoLocTruncTimeKey, StatsCounter> geoTimeGranRDD);
    
    public void saveGeoByTimeInterval(Date from,Date to,JavaPairRDD<GeoLocTruncKey, StatsCounter> geoTimeBoundRDD);
    
    public void saveHtsByTimeGran(JavaPairRDD<DateHtKey, StatsCounter> htTimeGranRDD);
    
    public void saveHtsByTimeInterval(Date from,Date to,JavaPairRDD<String, StatsCounter> htTimeBoundRDD);
   
}
