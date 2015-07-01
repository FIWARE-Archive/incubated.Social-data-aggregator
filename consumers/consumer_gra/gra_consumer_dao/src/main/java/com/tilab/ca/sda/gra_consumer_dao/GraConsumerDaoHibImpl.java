package com.tilab.ca.sda.gra_consumer_dao;

import com.tilab.ca.hibutils.Hibutils;
import com.tilab.ca.sda.gra_consumer_dao.data.StatsPreGenderGeo;
import com.tilab.ca.sda.gra_consumer_dao.data.StatsPreGenderGeoBound;
import com.tilab.ca.sda.gra_consumer_dao.data.StatsPreGenderHt;
import com.tilab.ca.sda.gra_consumer_dao.data.StatsPreGenderHtBound;
import com.tilab.ca.sda.gra_consumer_dao.data.TwGenderProfile;
import com.tilab.ca.sda.gra_consumer_dao.hibernate.GraSession;
import java.io.File;
import java.util.Iterator;
import java.util.Properties;
import org.apache.spark.api.java.JavaRDD;
import org.hibernate.cfg.Configuration;
import org.jboss.logging.Logger;


public class GraConsumerDaoHibImpl implements GraConsumerDao{
    
    
    private static final Logger log=Logger.getLogger(GraConsumerDaoHibImpl.class);
    
    public static final String HIBERNATE_CONF_FILE_NAME="gender-consumer-tw.cfg.xml";
    private final Configuration cfg;
    
    public GraConsumerDaoHibImpl(Properties props){
       String hibConfFilePath=props.getProperty(CONF_PATH_PROPS_KEY)+File.separator+HIBERNATE_CONF_FILE_NAME;
       cfg = new Configuration().configure(new File(hibConfFilePath));
    }
    
    public GraConsumerDaoHibImpl(String hibConfFilePath){
       cfg = new Configuration().configure(new File(hibConfFilePath));
    }

    @Override
    public void saveTwGenderProfiles(JavaRDD<TwGenderProfile> twGenderProfiles) {
        log.info(String.format("saving %d distinct profiles", twGenderProfiles.count()));
        twGenderProfiles.foreachPartition((spgIterator) ->{
            saveOnDb(spgIterator);
        });
    }

    @Override
    public void saveGeoByTimeGran(JavaRDD<StatsPreGenderGeo> genderGeoRDD) {
        log.info(String.format("saving %d geo objects", genderGeoRDD.count()));
        genderGeoRDD.foreachPartition((spgIterator) ->{
            saveOnDb(spgIterator);
        });
    }

    @Override
    public void saveGeoByTimeInterval(JavaRDD<StatsPreGenderGeoBound> genderGeoBoundRdd) {
        log.info(String.format("saving %d geo bounds objects", genderGeoBoundRdd.count()));
        genderGeoBoundRdd.foreachPartition((spgIterator) ->{
            saveOnDb(spgIterator);
        });
    }

    @Override
    public void saveHtsByTimeGran(JavaRDD<StatsPreGenderHt> genderHtRDD) {
        log.info(String.format("saving %d ht objects", genderHtRDD.count()));
        genderHtRDD.foreachPartition((spgIterator) ->{
            saveOnDb(spgIterator);
        });
    }

    @Override
    public void saveHtsByTimeInterval(JavaRDD<StatsPreGenderHtBound> genderHtBoundRdd) {
        log.info(String.format("saving %d ht bounds objects", genderHtBoundRdd.count()));
        genderHtBoundRdd.foreachPartition((spgIterator) ->{
            saveOnDb(spgIterator);
        });
    }
    
    private void saveOnDb(Iterator<?> objIterator) throws Exception{
        final Configuration hibConf=cfg;
        Hibutils.executeVoidOperation(GraSession.getSessionFactory(hibConf), 
            (session) ->{
                objIterator.forEachRemaining((obj) -> session.save(obj));
        });
    }
}
