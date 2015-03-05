package com.tilab.ca.sda.consumer.tw.tot.dao;


import com.tilab.ca.hibutils.Hibutils;
import com.tilab.ca.sda.consumer.tw.tot.core.TotTwConstants;
import com.tilab.ca.sda.consumer.tw.tot.core.data.DateHtKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.GeoLocTruncKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.GeoLocTruncTimeKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.StatsCounter;
import com.tilab.ca.sda.consumer.tw.tot.dao.hibernate.TwStatsSession;
import com.tilab.ca.sda.consumer.tw.tot.dao.hibernate.mapping.StatsPreGeo;
import com.tilab.ca.sda.consumer.tw.tot.dao.hibernate.mapping.StatsPreGeoBound;
import com.tilab.ca.sda.consumer.tw.tot.dao.hibernate.mapping.StatsPreHts;
import com.tilab.ca.sda.consumer.tw.tot.dao.hibernate.mapping.StatsPreHtsBound;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.hibernate.cfg.Configuration;
import org.jboss.logging.Logger;


public class ConsumerTwTotDaoDefaultImpl implements ConsumerTwTotDao{
    
    private static final Logger log=Logger.getLogger(ConsumerTwTotDaoDefaultImpl.class);
    
    private final Configuration cfg;
    
    public ConsumerTwTotDaoDefaultImpl(Properties props){
       String hibConfFilePath=props.getProperty(CONF_PATH_PROPS_KEY)+File.separator+TotTwConstants.HIBERNATE_CONF_FILE_NAME;
       cfg = new Configuration().configure(new File(hibConfFilePath));
    }
    
    public ConsumerTwTotDaoDefaultImpl(String hibConfFilePath){
       cfg = new Configuration().configure(new File(hibConfFilePath));
    }
    
    @Override
    public void saveGeoByTimeGran(JavaPairRDD<GeoLocTruncTimeKey, StatsCounter> geoTimeGranRDD){
        log.info("CALLED saveGeoByTimeGran");
        JavaRDD<StatsPreGeo> preGeoRDD=geoTimeGranRDD.map((t) -> new StatsPreGeo(t._1,t._2));
        preGeoRDD.foreachPartition((spgIterator) ->{
            saveOnDb(spgIterator);
        }); 
    }
    
    @Override
    public void saveGeoByTimeInterval(Date from,Date to,JavaPairRDD<GeoLocTruncKey, StatsCounter> geoTimeBoundRDD){
        log.info("CALLED saveGeoByTimeInterval");       
        JavaRDD<StatsPreGeoBound> preGeoBoundRDD=geoTimeBoundRDD.map((t) -> new StatsPreGeoBound(from,to,t._1,t._2));
		preGeoBoundRDD.foreachPartition((spgBoundIterator) ->{
            saveOnDb(spgBoundIterator);
        });         
    }
    
    @Override
    public void saveHtsByTimeGran(JavaPairRDD<DateHtKey, StatsCounter> htTimeGranRDD){
        log.info("CALLED saveGeoByTimeGran");
        JavaRDD<StatsPreHts> preHtsRDD=htTimeGranRDD.map((t) -> new StatsPreHts(t._1,t._2));
        preHtsRDD.foreachPartition((sphIterator) ->{
            saveOnDb(sphIterator);
        });
    }
    
    @Override
    public void saveHtsByTimeInterval(Date from,Date to,JavaPairRDD<String, StatsCounter> htTimeBoundRDD){
        log.info("CALLED saveGeoByTimeGran");
        JavaRDD<StatsPreHtsBound> preHtsRDD=htTimeBoundRDD.map((t) -> new StatsPreHtsBound(from,to,t._1,t._2));
        preHtsRDD.foreachPartition((sphIterator) ->{
            saveOnDb(sphIterator);
        });
    }
    
  
    private void saveOnDb(Iterator<?> objIterator) throws Exception{
        final Configuration hibConf=cfg;
        Hibutils.executeVoidOperation(TwStatsSession.getSessionFactory(hibConf), 
            (session) ->{
                objIterator.forEachRemaining((obj) -> session.save(obj));
        });
    }
}
