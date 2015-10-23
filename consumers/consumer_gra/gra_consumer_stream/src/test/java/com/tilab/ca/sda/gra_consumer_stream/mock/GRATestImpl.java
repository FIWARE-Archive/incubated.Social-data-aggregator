package com.tilab.ca.sda.gra_consumer_stream.mock;

import com.tilab.ca.sda.gra_core.GenderTypes;
import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.gra_core.components.GRA;
import com.tilab.ca.sda.gra_core.components.GRAConfig;
import com.tilab.ca.sda.sda.model.TwUserProfile;
import java.util.Map;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;


public class GRATestImpl implements GRA{

    private Map<Long,GenderTypes> gtMap=null;
    
    public GRATestImpl(Map<Long,GenderTypes> gtMap) {
        this.gtMap=gtMap;
    }

    @Override
    public void init(GRAConfig graConf, JavaSparkContext jsc) {}

    @Override
    public JavaRDD<ProfileGender> evaluateProfiles(JavaRDD<TwUserProfile> twProfilesRdd) { //,JavaSparkContext jsc
        return twProfilesRdd.map(twProfile -> new ProfileGender(twProfile, 
                gtMap.getOrDefault(twProfile.getUid(), GenderTypes.UNKNOWN)));
    }
    
}
