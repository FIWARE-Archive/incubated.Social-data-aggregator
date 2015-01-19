package com.tilab.ca.sda.ctw;

import com.tilab.ca.sda.ctw.bus.BusConnection;
import com.tilab.ca.sda.ctw.bus.ProducerFactory;
import com.tilab.ca.sda.ctw.mocks.TwCollectorTestProps;
import com.tilab.ca.sda.ctw.dao.TwStatsDao;
import com.tilab.ca.sda.ctw.mocks.ProducerFactoryTestImpl.SendContent;
import com.tilab.ca.sda.ctw.mocks.ProducerFactoryTestImpl.TestProducer;
import com.tilab.ca.spark_test_lib.streaming.SparkStreamingTest;
import com.tilab.ca.spark_test_lib.streaming.annotations.SparkTestConfig;
import java.io.File;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

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
    
    @Test
    public void testCreationCustomProducerFactoryImpl(){
        final String testTopic="test";
        final String testMessage="testMessage";
        final String testKey="testKey";
        
        try {
            ProducerFactory<String,String> mockProdFact=TwStreamConnectorMain.createProducerFactory(props,new Properties());
            BusConnection<String,String> conn=mockProdFact.newInstance();
            conn.send(testTopic, testMessage);
            
            TestProducer tp=(TestProducer)conn;
            SendContent<String,String> sc=(SendContent)tp.outputList.get(0);
            assertEquals(testTopic,sc.topic);
            assertEquals(testMessage,sc.msg);
            
            sc=(SendContent)tp.outputList.get(1);
            assertEquals(testTopic,sc.topic);
            assertEquals(testMessage,sc.msg);
            assertEquals(testKey,sc.key);
            
        } catch (Exception ex) {
            Logger.getLogger(MappingAndBusTestCase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
