/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.gra_core.components;

import com.tilab.ca.sda.gra_core.DescrResults;
import com.tilab.ca.sda.gra_core.GenderTypes;
import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.gra_core.components.mock.FeatureExtractorTest;
import com.tilab.ca.sda.gra_core.components.mock.MlModelTest;
import com.tilab.ca.sda.sda.model.TwUserProfile;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author dino
 */
public class GenderUserDescrTest {

    private final String BASE_PATH;
    private JavaSparkContext jsc;
    
    public GenderUserDescrTest() {
        
        String workingDir = System.getProperty("user.dir");
        
        BASE_PATH = String.format("%s#src#test#resources#",
                workingDir).replace("#", File.separator);
    }

    @Before
    public void setUp() {
        jsc=new JavaSparkContext("local", "testDescr");
    }
    
    @After
    public void tearDown() {
        jsc.stop();
    }
    
    /**
     * Test of processDescriptionText method, of class GenderUserDescr.
    */ 
    @Test
    public void testProcessDescriptionText() {
        System.out.println("processDescriptionText");
        String description = "description containing @mention #hashtag 1235 :)";
        String expResult = "description containing hashtag :)";
        String result = GenderUserDescr.processDescriptionText(description);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testProcessDescriptionTextWithUrls() {
        System.out.println("processDescriptionText");
        String description = "description http://www.google.it and www.anothersite.com and https://thirdsite.net";
        String expResult = "description and and";
        String result = GenderUserDescr.processDescriptionText(description);
        assertEquals(expResult, result);
    }

    /**
     * Test of cleanWord method, of class GenderUserDescr.
    */
    @Test
    public void testCleanWord() {
        System.out.println("cleanWord");
        GenderUserDescr instance = new GenderUserDescr(new MlModelTest(null), new FeatureExtractorTest(), jsc, BASE_PATH);
        assertEquals("test", instance.cleanWord("test()"));
        assertEquals("bright", instance.cleanWord("bright([])"));
        assertEquals("test2", instance.cleanWord("_test2_"));
        assertEquals("alba", instance.cleanWord("al_ba"));
    }

    /**
     * Test of isOkAsKey method, of class GenderUserDescr.
    */
    @Test
    public void testIsOkAsKey() {
        System.out.println("isOkAsKey");
        GenderUserDescr instance = new GenderUserDescr(new MlModelTest(null), new FeatureExtractorTest(), jsc, BASE_PATH);
        assertEquals(true, instance.isOkAsKey("test"));
        assertEquals(false, instance.isOkAsKey("and")); //stop word
        assertEquals(false, instance.isOkAsKey("to")); //word too short
        assertEquals(true, instance.isOkAsKey("gorgon")); 
    }
 
    /**
     * Test of processDescription method, of class GenderUserDescr.
    */ 
    @Test
    public void testProcessDescription() {
        System.out.println("processDescription");
        GenderUserDescr instance = new GenderUserDescr(new MlModelTest(null), new FeatureExtractorTest(), jsc, BASE_PATH);
        List<String> result = instance.processDescription("the pen is on the table. Right @mension?");
        String[] expResult ={"pen","table","right"};
        assertEquals(Arrays.asList(expResult), result);
    }

    /**
     * Test of getGendersFromTwProfiles method, of class GenderUserDescr.
    */
    @Test
    public void testGetGendersFromTwProfiles() throws Exception{
        System.out.println("getGendersFromTwProfiles");
        double[] predictions={0,0,0,1,2};
        GenderUserDescr instance = new GenderUserDescr(new MlModelTest(predictions), new FeatureExtractorTest(5,jsc), jsc, BASE_PATH);
        List<ProfileGender> profilesGenderLst=new ArrayList<>();
        profilesGenderLst.add(new ProfileGender(new TwUserProfile(1L,"just to have a description"), GenderTypes.UNKNOWN));
        profilesGenderLst.add(new ProfileGender(new TwUserProfile(2L,"description 2"), GenderTypes.UNKNOWN));
        profilesGenderLst.add(new ProfileGender(new TwUserProfile(3L,"description 3"), GenderTypes.UNKNOWN));
        profilesGenderLst.add(new ProfileGender(new TwUserProfile(4L,"description 4"), GenderTypes.UNKNOWN));
        profilesGenderLst.add(new ProfileGender(new TwUserProfile(5L,"description 5"), GenderTypes.UNKNOWN));
        
        profilesGenderLst.add(new ProfileGender(new TwUserProfile(6L), GenderTypes.UNKNOWN));
        JavaRDD<ProfileGender> profilesRDD = jsc.parallelize(profilesGenderLst);
        DescrResults result = instance.getGendersFromTwProfiles(profilesRDD);
        
        List<ProfileGender> resNotRecon=result.getProfilesUnrecognized().collect();
        assertEquals(1, resNotRecon.size());
        assertEquals(GenderTypes.UNKNOWN, resNotRecon.get(0).getGender());
        
        List<ProfileGender> resRecon=result.getProfilesRecognized().collect();
        assertEquals(5, resRecon.size());
        assertEquals(GenderTypes.MALE, resRecon.get(0).getGender());
        assertEquals(GenderTypes.MALE, resRecon.get(1).getGender());
        assertEquals(GenderTypes.MALE, resRecon.get(2).getGender());
        assertEquals(GenderTypes.FEMALE, resRecon.get(3).getGender());
        assertEquals(GenderTypes.PAGE, resRecon.get(4).getGender());
    }
    
}
