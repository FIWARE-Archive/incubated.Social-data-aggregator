/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.tw_user_profile_extractor;

import com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping.TwProfile;
import com.tilab.ca.spark_test_lib.batch.SparkBatchTest;
import com.tilab.ca.spark_test_lib.streaming.annotations.SparkTestConfig;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Test;
import static org.junit.Assert.*;


@SparkTestConfig(appName="testExtractUsrProfiles",master="local[2]")
public class TwProfileExtractorTest extends SparkBatchTest{

    public TwProfileExtractorTest() {
        super(TwProfileExtractorTest.class);
    }
    
    /**
     * Test of extractUsrProfiles method, of class TwProfileExtractor.
     */
    @Test
    public void testExtractUsrProfiles() {
        System.out.println("extractUsrProfiles");
        JavaSparkContext sc = null;
        String inputDataPath = "";
        boolean testSetProfiles = false;
        JavaRDD<TwProfile> expResult = null;
        JavaRDD<TwProfile> result = TwProfileExtractor.extractUsrProfiles(sc, inputDataPath, testSetProfiles);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    
    
}
