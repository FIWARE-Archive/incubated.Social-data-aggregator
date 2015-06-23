package com.tilab.ca.sda.gra_consumer_stream.mock;

import com.tilab.ca.sda.gra_consumer_dao.GraConsumerDao;
import com.tilab.ca.sda.gra_consumer_dao.data.StatsPreGenderGeo;
import com.tilab.ca.sda.gra_consumer_dao.data.StatsPreGenderGeoBound;
import com.tilab.ca.sda.gra_consumer_dao.data.StatsPreGenderHt;
import com.tilab.ca.sda.gra_consumer_dao.data.StatsPreGenderHtBound;
import com.tilab.ca.sda.gra_consumer_dao.data.TwGenderProfile;
import com.tilab.ca.spark_test_lib.streaming.interfaces.ExpectedOutputHandler;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;


public class GraDaoOutputHandler implements ExpectedOutputHandler,GraConsumerDao{

    public static final int TEST_PROFILES=0;
    public static final int TEST_GEO=1;
    public static final int TEST_HT=2;
    
   
    private List<TwGenderProfile> resGenderProfiles;
    private List<StatsPreGenderGeo> resGenderGeo;
    private List<StatsPreGenderHt> resGenderHt;
    
    public GraDaoOutputHandler(int testType,int expectedOutputSize){
        
    }
    
    @Override
    public boolean isExpectedOutputFilled() {
        return true;
    }

    @Override
    public void saveTwGenderProfiles(JavaRDD<TwGenderProfile> twGenderProfiles) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveGeoByTimeGran(JavaRDD<StatsPreGenderGeo> genderGeoRDD) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveGeoByTimeInterval(JavaRDD<StatsPreGenderGeoBound> genderGeoBoundRdd) {
        //not used on streaming consumer
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveHtsByTimeGran(JavaRDD<StatsPreGenderHt> genderHtRDD) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveHtsByTimeInterval(JavaRDD<StatsPreGenderHtBound> genderHtBoundRdd) {
        //not used on streaming consumer
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
