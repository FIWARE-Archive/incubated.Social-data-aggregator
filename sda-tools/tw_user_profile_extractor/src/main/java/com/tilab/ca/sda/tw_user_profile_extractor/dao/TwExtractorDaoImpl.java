package com.tilab.ca.sda.tw_user_profile_extractor.dao;

import com.tilab.ca.hibutils.Hibutils;
import com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping.TwProfile;
import java.io.File;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.hibernate.cfg.Configuration;

public class TwExtractorDaoImpl implements TwExtractorDao{

    private static final Logger log = Logger.getLogger(TwExtractorDaoImpl.class);
    private final Configuration cfg;

    public TwExtractorDaoImpl(String hibConfFilePath) {
        this.cfg = new Configuration().configure(new File(hibConfFilePath));
    }

    @Override
    public void saveTwProfilesOnStorage(JavaRDD<TwProfile> twProfiles, String outputFolder) {
        if (StringUtils.isNotBlank(outputFolder)) {
            saveTwProfilesOnFile(twProfiles, outputFolder);
        } else {
            saveTwProfileOnDb(twProfiles);
        }
    }

    private void saveTwProfileOnDb(JavaRDD<TwProfile> twProfiles) {
        final Configuration hibConf = cfg;
        log.info("saving twProfiles objects on database for each rdd partition...");
        try {
            twProfiles.foreachPartition((twProfilesIterator) -> {
                Hibutils.executeVoidOperation(TwProfileExtractorSession.getSessionFactory(hibConf),
                        (session) -> {
                            twProfilesIterator.forEachRemaining((twProfile) -> session.saveOrUpdate(twProfile));
                        });
            });

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void saveTwProfilesOnFile(JavaRDD<TwProfile> twProfiles, String outputFolder) {
        log.info("saving twProfiles objects on path " + outputFolder);
        twProfiles.saveAsTextFile(outputFolder);
    }
}
