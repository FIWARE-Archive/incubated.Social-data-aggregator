package com.tilab.ca.sda.tw_user_profile_extractor.dao;

import com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping.TwProfile;
import org.apache.spark.api.java.JavaRDD;

public interface TwExtractorDao {
    public void saveTwProfilesOnStorage(JavaRDD<TwProfile> twProfiles, String outputFolder);
}
