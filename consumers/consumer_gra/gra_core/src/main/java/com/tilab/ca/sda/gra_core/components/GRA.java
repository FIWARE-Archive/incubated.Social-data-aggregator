package com.tilab.ca.sda.gra_core.components;

import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.sda.model.TwUserProfile;
import java.io.Serializable;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;


public interface GRA extends Serializable{
    
    public void init(GRAConfig graConf,JavaSparkContext jsc);
    
    public JavaRDD<ProfileGender> evaluateProfiles(JavaRDD<TwUserProfile> twProfilesRdd);
    
}
