package com.tilab.ca.sda.ctw;

import com.tilab.ca.sda.ctw.mocks.TwStatsDaoTestImpl;
import com.tilab.ca.sda.ctw.mocks.TwCollectorTestProps;
import com.tilab.ca.sda.ctw.dao.TwStatsDao;
import com.tilab.ca.spark_test_lib.streaming.SparkStreamingTest;
import com.tilab.ca.spark_test_lib.streaming.annotations.SparkTestConfig;
import com.tilab.ca.spark_test_lib.streaming.interfaces.ExpectedOutputHandler;
import com.tilab.ca.spark_test_lib.streaming.utils.TestStreamUtils;
import java.io.File;
import java.io.Serializable;
import org.apache.spark.streaming.api.java.JavaDStream;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import twitter4j.Status;
import twitter4j.TwitterObjectFactory;

@SparkTestConfig(appName = "GenericSparkTest", master = "local[2]", batchDurationMillis = 30000, useManualClock = true)
public class GenericTestCase extends SparkStreamingTest implements Serializable {

    private TwStreamConnectorProperties props;
    private TwStatsDao twsd = null;
    private final String BASE_PATH;

    public GenericTestCase() {
        super(GenericTestCase.class);
        props = new TwCollectorTestProps();
        String workingDir = System.getProperty("user.dir");
        /*
         Each file represents the content of a batch
        */
        BASE_PATH = String.format("%s#src#test#resources#data#",
                workingDir).replace("#", File.separator);
    }

    
    @Test
    public void simpleTestTwoWindows() {
        twsd = new TwStatsDaoTestImpl(9);
        int windowNumBatches=4;
        $newTest()
                .expectedOutputHandler((ExpectedOutputHandler)twsd)
                .sparkStreamJob((jssc,eoh) -> {

                    JavaDStream<Status> mockTwStream = 
                            TestStreamUtils.createMockDStreamFromDir(jssc, 1, BASE_PATH)
                                            .map((rawJson) -> TwitterObjectFactory.createStatus(rawJson));
                
                    
                    new TwitterStreamConnector(props, twsd)
                            .collectAndSaveTweets(mockTwStream);
                    
                })
                .test((eoh) -> {
                    TwStatsDaoTestImpl tws=(TwStatsDaoTestImpl)eoh;
                    assertEquals(9, tws.mockTwStorage.size());
                    assertEquals(1, tws.mockTwStorage.get(0).getId());
                    assertEquals(1, tws.mockTwStorage.get(0).getUser().getId());
                    
                    assertEquals(2, tws.mockTwStorage.get(1).getId());
                    assertEquals(2, tws.mockTwStorage.get(1).getUser().getId());
                    
                    assertEquals(3, tws.mockTwStorage.get(2).getId());
                    assertEquals(3, tws.mockTwStorage.get(2).getUser().getId());  
                    
                    assertEquals(4, tws.mockTwStorage.get(3).getId());
                    assertEquals(4, tws.mockTwStorage.get(3).getUser().getId());       
                    
                    assertEquals(5, tws.mockTwStorage.get(4).getId());
                    assertEquals(5, tws.mockTwStorage.get(4).getUser().getId());       
                    
                    assertEquals(6, tws.mockTwStorage.get(5).getId());
                    assertEquals(6, tws.mockTwStorage.get(5).getUser().getId());
                    
                    assertEquals(7, tws.mockTwStorage.get(6).getId());
                    assertEquals(7, tws.mockTwStorage.get(6).getUser().getId());       
                    
                    assertEquals(8, tws.mockTwStorage.get(7).getId());
                    assertEquals(8, tws.mockTwStorage.get(7).getUser().getId());       
                    
                    assertEquals(9, tws.mockTwStorage.get(8).getId());
                    assertEquals(9, tws.mockTwStorage.get(8).getUser().getId());       
                }).executeTest(windowNumBatches, 10000);
    }
    
     
    @Test
    public void simpleTestOneWindow() {
        twsd = new TwStatsDaoTestImpl(6);
        int windowNumBatches=2;
        
        $newTest()
                .expectedOutputHandler((ExpectedOutputHandler)twsd)
                .sparkStreamJob((jssc,eoh) -> {

                    JavaDStream<Status> mockTwStream = 
                            TestStreamUtils.createMockDStreamFromDir(jssc, 1, BASE_PATH)
                                            .map((rawJson) -> TwitterObjectFactory.createStatus(rawJson));
                
                    
                    new TwitterStreamConnector(props, twsd)
                            .collectAndSaveTweets(mockTwStream);
                    
                })
                .test((eoh) -> {
                    TwStatsDaoTestImpl tws=(TwStatsDaoTestImpl)eoh;
                    assertEquals(6, tws.mockTwStorage.size());
                    assertEquals(1, tws.mockTwStorage.get(0).getId());
                    assertEquals(1, tws.mockTwStorage.get(0).getUser().getId());
                    
                    assertEquals(2, tws.mockTwStorage.get(1).getId());
                    assertEquals(2, tws.mockTwStorage.get(1).getUser().getId());
                    
                    assertEquals(3, tws.mockTwStorage.get(2).getId());
                    assertEquals(3, tws.mockTwStorage.get(2).getUser().getId());  
                    
                    assertEquals(4, tws.mockTwStorage.get(3).getId());
                    assertEquals(4, tws.mockTwStorage.get(3).getUser().getId());       
                    
                    assertEquals(5, tws.mockTwStorage.get(4).getId());
                    assertEquals(5, tws.mockTwStorage.get(4).getUser().getId());       
                    
                    assertEquals(6, tws.mockTwStorage.get(5).getId());
                    assertEquals(6, tws.mockTwStorage.get(5).getUser().getId());       
                }).executeTest(windowNumBatches, 10000);
    }
    

}
