package com.tilab.ca.sda.gra_consumer_dao;

import com.tilab.ca.sda.gra_consumer_dao.data.StatsPreGenderGeo;
import com.tilab.ca.sda.gra_consumer_dao.data.StatsPreGenderGeoBound;
import com.tilab.ca.sda.gra_consumer_dao.data.StatsPreGenderHt;
import com.tilab.ca.sda.gra_consumer_dao.data.StatsPreGenderHtBound;
import com.tilab.ca.sda.gra_consumer_dao.data.TwGenderProfile;
import java.io.File;
import java.util.Properties;
import org.apache.spark.api.java.JavaRDD;
import org.jboss.logging.Logger;

public class GraConsumerDaoFileImpl implements GraConsumerDao{

    public static final String OUTPUT_FILES_PATH_PROP="graOutputFilesPath";
    
    public static final String OUTPUT_FILE_PROFILES_NAME="profiles_";
    public static final String OUTPUT_FILE_GEO_NAME="gender_geo_";
    public static final String OUTPUT_FILE_GEO_BOUND_NAME="gender_geo_bound_";
    public static final String OUTPUT_FILE_HT_NAME="gender_ht_";
    public static final String OUTPUT_FILE_HT_BOUND_NAME="gender_ht_bound_";
    
    
    private final String outputPathName;
    
    private static final Logger log=Logger.getLogger(GraConsumerDaoFileImpl.class);
    
    public GraConsumerDaoFileImpl(Properties props) {
        outputPathName=props.getProperty(OUTPUT_FILES_PATH_PROP);
    }

    @Override
    public void saveTwGenderProfiles(JavaRDD<TwGenderProfile> twGenderProfiles) {
        log.info(String.format("saving %d distinct profiles", twGenderProfiles.count()));
        twGenderProfiles.saveAsTextFile(outputPathName+File.separator+OUTPUT_FILE_PROFILES_NAME+
                                        System.currentTimeMillis());
    }

    @Override
    public void saveGeoByTimeGran(JavaRDD<StatsPreGenderGeo> genderGeoRDD) {
        log.info(String.format("saving %d geo objects", genderGeoRDD.count()));
        genderGeoRDD.saveAsTextFile(outputPathName+File.separator+OUTPUT_FILE_GEO_NAME+System.currentTimeMillis());
    }

    @Override
    public void saveGeoByTimeInterval(JavaRDD<StatsPreGenderGeoBound> genderGeoBoundRdd) {
        log.info(String.format("saving %d geo bounds objects", genderGeoBoundRdd.count()));
        genderGeoBoundRdd.saveAsTextFile(outputPathName+File.separator+OUTPUT_FILE_GEO_BOUND_NAME+System.currentTimeMillis());
    }

    @Override
    public void saveHtsByTimeGran(JavaRDD<StatsPreGenderHt> genderHtRDD) {
        log.info(String.format("saving %d ht objects", genderHtRDD.count()));
        genderHtRDD.saveAsTextFile(outputPathName+File.separator+OUTPUT_FILE_HT_NAME+System.currentTimeMillis());
    }

    @Override
    public void saveHtsByTimeInterval(JavaRDD<StatsPreGenderHtBound> genderHtBoundRdd) {
        log.info(String.format("saving %d ht bounds objects", genderHtBoundRdd.count()));
        genderHtBoundRdd.saveAsTextFile(outputPathName+File.separator+OUTPUT_FILE_HT_BOUND_NAME+System.currentTimeMillis());
    }
    
}

       