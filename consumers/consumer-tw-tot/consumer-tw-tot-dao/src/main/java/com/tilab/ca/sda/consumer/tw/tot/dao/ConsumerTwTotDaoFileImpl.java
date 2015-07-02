package com.tilab.ca.sda.consumer.tw.tot.dao;

import com.tilab.ca.sda.consumer.tw.tot.core.data.StatsCounter;
import com.tilab.ca.sda.consumer.tw.tot.dao.hibernate.mapping.StatsPreGeo;
import com.tilab.ca.sda.consumer.tw.tot.dao.hibernate.mapping.StatsPreGeoBound;
import com.tilab.ca.sda.consumer.tw.tot.dao.hibernate.mapping.StatsPreHts;
import com.tilab.ca.sda.consumer.tw.tot.dao.hibernate.mapping.StatsPreHtsBound;
import com.tilab.ca.sda.sda.model.keys.DateHtKey;
import com.tilab.ca.sda.sda.model.keys.GeoLocTruncKey;
import com.tilab.ca.sda.sda.model.keys.GeoLocTruncTimeKey;
import java.io.File;
import java.util.Date;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;


public class ConsumerTwTotDaoFileImpl implements ConsumerTwTotDao{

    public static final String OUTPUT_FILES_PATH_PROP="totTwOutputFilesPath";
    
    public static final String COMPONENT_NAME="tw_tot_";
    
    public static final String OUTPUT_FILE_GEO_NAME=COMPONENT_NAME+"geo_";
    public static final String OUTPUT_FILE_GEO_BOUND_NAME=COMPONENT_NAME+"geo_bound_";
    public static final String OUTPUT_FILE_HT_NAME=COMPONENT_NAME+"ht_";
    public static final String OUTPUT_FILE_HT_BOUND_NAME=COMPONENT_NAME+"ht_bound_";
    
    private static final Logger log = Logger.getLogger(ConsumerTwTotDao.class);
    
    private final String outputPathName;
    
    public ConsumerTwTotDaoFileImpl(Properties props) {
        outputPathName=props.getProperty(OUTPUT_FILES_PATH_PROP);
    }
    
    @Override
    public void saveGeoByTimeGran(JavaPairRDD<GeoLocTruncTimeKey, StatsCounter> geoTimeGranRDD, int gran) {
        log.info("CALLED saveGeoByTimeGran");
        JavaRDD<StatsPreGeo> preGeoRDD=geoTimeGranRDD.map((t) -> new StatsPreGeo(t._1,t._2,gran));
        log.info(String.format("saving %d geo objects with gran %d",preGeoRDD.count(),gran));
        preGeoRDD.saveAsTextFile(outputPathName+File.separator+OUTPUT_FILE_GEO_NAME+
                                        System.currentTimeMillis());
    }

    @Override
    public void saveGeoByTimeInterval(Date from, Date to, JavaPairRDD<GeoLocTruncKey, StatsCounter> geoTimeBoundRDD) {
        log.info("CALLED saveGeoByTimeInterval");       
        JavaRDD<StatsPreGeoBound> preGeoBoundRDD=geoTimeBoundRDD.map((t) -> new StatsPreGeoBound(from,to,t._1,t._2));
        
        log.info(String.format("saving %d geo objects with from %s and to %s",preGeoBoundRDD.count(),from.toString(),to.toString()));
        preGeoBoundRDD.saveAsTextFile(outputPathName+File.separator+OUTPUT_FILE_GEO_BOUND_NAME+
                                        System.currentTimeMillis());
    }

    @Override
    public void saveHtsByTimeGran(JavaPairRDD<DateHtKey, StatsCounter> htTimeGranRDD, int gran) {
        log.info("CALLED saveHtsByTimeGran");
        JavaRDD<StatsPreHts> preHtsRDD=htTimeGranRDD.map((t) -> new StatsPreHts(t._1,t._2,gran));
        log.info(String.format("saving %d hts objects with gran %d",preHtsRDD.count(),gran));
        preHtsRDD.saveAsTextFile(outputPathName+File.separator+OUTPUT_FILE_HT_NAME+
                                        System.currentTimeMillis());
        
    }

    @Override
    public void saveHtsByTimeInterval(Date from, Date to, JavaPairRDD<String, StatsCounter> htTimeBoundRDD) {
        log.info("CALLED saveHtsByTimeInterval");
        JavaRDD<StatsPreHtsBound> preHtsRDD=htTimeBoundRDD.map((t) -> new StatsPreHtsBound(from,to,t._1,t._2));
        
        log.info(String.format("saving %d hts objects with from %s and to %s",preHtsRDD.count(),from.toString(),to.toString()));
        preHtsRDD.saveAsTextFile(outputPathName+File.separator+OUTPUT_FILE_HT_BOUND_NAME+
                                        System.currentTimeMillis());
    }
    
}
