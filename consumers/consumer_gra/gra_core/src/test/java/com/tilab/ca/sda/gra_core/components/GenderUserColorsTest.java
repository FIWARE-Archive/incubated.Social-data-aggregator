package com.tilab.ca.sda.gra_core.components;

import com.tilab.ca.sda.gra_core.GenderTypes;
import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.gra_core.components.mock.MlModelTest;
import com.tilab.ca.sda.sda.model.TwUserProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;



public class GenderUserColorsTest {
    
    private JavaSparkContext jsc;
   
    
    public GenderUserColorsTest() {
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
     * Test of getGenderFromProfileColours method, of class GenderUserColors.
     */
    @Test
    public void testGetGenderFromProfileColours() {
        System.out.println("getGenderFromProfileColours");
        TwUserProfile twUserProfile = new TwUserProfile(1L);
        String[] profileColors={"00CCFF","000000","111111","FFFFFF","DDCCFF","EE11FF"};
        twUserProfile.setProfileColors(profileColors);
        double[] predictions={0f};
        GenderUserColors instance = new GenderUserColors(9, 4, new MlModelTest(predictions), jsc, "");
        GenderTypes expResult = GenderTypes.MALE;
        GenderTypes result = instance.getGenderFromProfileColours(twUserProfile);
        assertEquals(expResult, result);  
    }
    
    /**
     * Test of getGendersFromTwProfiles method, of class GenderUserColors.
    */
    @Test
    public void testGetGendersFromTwProfiles() {
        System.out.println("getGendersFromTwProfiles");
        List<ProfileGender> profilesGenderLst=new ArrayList<>();
        String[] profileColors={"00CCFF","000000","111111","FFFFFF","DDCCFF","EE11FF"};
        profilesGenderLst.add(new ProfileGender(new TwUserProfile(1L), GenderTypes.UNKNOWN));
        profilesGenderLst.get(0).getTwProfile().setProfileColors(profileColors);
        profilesGenderLst.add(new ProfileGender(new TwUserProfile(2L), GenderTypes.UNKNOWN));
        profilesGenderLst.get(1).getTwProfile().setProfileColors(profileColors);
        double[] predictions={0f,1f};
        GenderUserColors instance = new GenderUserColors(9, 4, new MlModelTest(predictions), jsc, "");
        
        JavaRDD<ProfileGender> profilesRDD = jsc.parallelize(profilesGenderLst);
        JavaRDD<ProfileGender> result = instance.getGendersFromTwProfiles(profilesRDD);
        List<GenderTypes> gt=result.collect().stream().map(profileGender -> profileGender.getGender()).collect(Collectors.toList());
        assertEquals(2, gt.size());
        assertEquals(GenderTypes.MALE, gt.get(0));
        assertEquals(GenderTypes.FEMALE, gt.get(1));
    }
 
}
