package com.tilab.ca.sda.ctw;

import com.tilab.ca.sda.ctw.dao.TwStatsDao;
import com.tilab.ca.spark_test_lib.streaming.SparkStreamingTest;
import com.tilab.ca.spark_test_lib.streaming.annotations.SparkTestConfig;
import java.io.File;
import java.io.Serializable;

/**
 * Test that mapping between raw tweets  
 * and the internal model is done right and 
 * that data are sent on the mock bus
 */

@SparkTestConfig(appName = "GenericSparkTest", master = "local[2]", batchDurationMillis = 30000, useManualClock = true)
public class MappingAndBusTestCase extends SparkStreamingTest implements Serializable{
    
    private TwStreamConnectorProperties props;
    private TwStatsDao twsd = null;
    private final String BASE_PATH;
    
    public MappingAndBusTestCase(){
        super(MappingAndBusTestCase.class);
        props = new TwCollectorTestProps();
        String workingDir = System.getProperty("user.dir");
        /*
         Each file represents the content of a batch
        */
        BASE_PATH = String.format("%s#src#test#resources#data#",
                workingDir).replace("#", File.separator);
    }
    
    
    
}
