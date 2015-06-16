package com.tilab.ca.sda.gra_consumer_dao;

import com.tilab.ca.sda.gra_consumer_dao.data.TwGenderProfile;
import java.io.Serializable;
import org.apache.spark.api.java.JavaRDD;


public interface GraConsumerDao extends Serializable{
    
    public static final String CONF_PATH_PROPS_KEY="confPath";
    
    public void saveTwGenderProfiles(JavaRDD<TwGenderProfile> twGenderProfiles);
}
