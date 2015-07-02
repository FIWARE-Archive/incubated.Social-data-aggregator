/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.tw_user_profile_extractor;

import com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping.TwProfile;
import com.tilab.ca.spark_test_lib.batch.SparkBatchTest;
import com.tilab.ca.spark_test_lib.streaming.annotations.SparkTestConfig;
import com.tilab.ca.spark_test_lib.streaming.interfaces.ExpectedOutputHandler;
import java.io.File;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import org.junit.Test;
import static org.junit.Assert.*;


@SparkTestConfig(appName="testExtractUsrProfiles",master="local[2]")
public class TwProfileExtractorTest extends SparkBatchTest{

    private final String BASE_PATH;
    
    public TwProfileExtractorTest() {
        super(TwProfileExtractorTest.class);
        String workingDir = System.getProperty("user.dir");
        BASE_PATH = String.format("%s#src#test#resources#",
                workingDir).replace("#", File.separator);
    }
    
    /**
     * Test of extractUsrProfiles method, of class TwProfileExtractor.
     */
    //2834
    @Test
    public void testExtractUsrProfiles() {
        $newBatchTest()
             .sparkTest((sc) -> {
                 JavaRDD<TwProfile> resRDD=TwProfileExtractor
                                              .extractUsrProfiles(sc, BASE_PATH+File.separator+"testProfileExtractorData.txt", false);
                 return resRDD.collect();
             })
             .test((res) ->{
                 List<TwProfile> twProfilesOutputList=(List<TwProfile>)res;
                 assertNotNull(twProfilesOutputList);
                 assertEquals("expected 3 distinct users",3, twProfilesOutputList.size());
                 twProfilesOutputList.sort((tp1,tp2) -> ((Long)tp1.getUserId()).compareTo(tp2.getUserId()));
                 assertEquals(1L, twProfilesOutputList.get(0).getUserId());
                 assertEquals(2L, twProfilesOutputList.get(1).getUserId());
                 assertEquals(3L, twProfilesOutputList.get(2).getUserId());
                 
                 assertEquals("Expected to have the last updated description for user 1","descrUpd1", twProfilesOutputList.get(0).getDescription());
             })
             .execute();
    }
}
