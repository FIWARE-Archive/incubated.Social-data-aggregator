package com.tilab.ca.sda.tw_user_profile_extractor.dao;

import com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping.TwProfile;
import java.io.File;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.hibernate.cfg.Configuration;


public class TwExtractorDaoImpl {
   
    private static final Logger log=Logger.getLogger(TwExtractorDaoImpl.class);
    private final Configuration cfg;

    public TwExtractorDaoImpl(String hibConfFilePath) {
        this.cfg = new Configuration().configure(new File(hibConfFilePath));
    }
    
    public void saveTwProfilesOnStorage(JavaRDD<TwProfile> twProfiles,String outputFolder){
        if(StringUtils.isNotBlank(outputFolder))
            saveTwProfilesOnFile(twProfiles, outputFolder);
        else
            saveTwProfileOnDb(twProfiles);
    }
    
    private void saveTwProfileOnDb(JavaRDD<TwProfile> twProfiles){
        
    }
    
    private void saveTwProfilesOnFile(JavaRDD<TwProfile> twProfiles,String outputFolder){
        log.info("saving twProfiles objects on path "+outputFolder);
        twProfiles.saveAsTextFile(outputFolder);
    }
}
