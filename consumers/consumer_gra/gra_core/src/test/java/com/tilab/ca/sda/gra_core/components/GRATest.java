package com.tilab.ca.sda.gra_core.components;

import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.gra_core.components.mock.FeatureExtractorTest;
import com.tilab.ca.sda.gra_core.components.mock.MlModelTest;
import com.tilab.ca.sda.gra_core.components.mock.NamesGenderMapTest;
import com.tilab.ca.sda.sda.model.TwUserProfile;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class GRATest implements Serializable{
    
    private final String BASE_PATH;
    private JavaSparkContext jsc;
    
    public GRATest() {
        String workingDir = System.getProperty("user.dir");
        
        BASE_PATH = String.format("%s#src#test#resources#",
                workingDir).replace("#", File.separator);
    }
    
    @Before
    public void setUp() {
        jsc=new JavaSparkContext("local", "testGRA");
    }
    
    @After
    public void tearDown() {
        jsc.stop();
    }

    /**
     * Test of waterfallGraEvaluation method, of class GRA.
     */
    @Test
    public void testWaterfallGraEvaluation() throws Exception {
        System.out.println("waterfallGraEvaluation");
        
        String[] profileColors={"00CCFF","000000","111111","FFFFFF","DDCCFF","EE11FF"};
        List<TwUserProfile> twUserProfileLst=new ArrayList<>();
        
        twUserProfileLst.add(new TwUserProfile(1L,"John Snow","johnsnow","I know nothing",profileColors));
        twUserProfileLst.add(new TwUserProfile(2L,"mmurdock","mattmurdock","devil inside",profileColors));
        twUserProfileLst.add(new TwUserProfile(3L,"male1","wolverine","just to have a description",profileColors));
        
        twUserProfileLst.add(new TwUserProfile(4L,"female1","user1","just to have a description",profileColors));
        twUserProfileLst.add(new TwUserProfile(5L,"female2","user2","just to have a description",profileColors));
        twUserProfileLst.add(new TwUserProfile(6L,"page1","page1","just to have a description",profileColors));
       
        twUserProfileLst.add(new TwUserProfile(7L,"male2","user4",null,profileColors));
        twUserProfileLst.add(new TwUserProfile(8L,"female3","user5",null,profileColors));
        twUserProfileLst.add(new TwUserProfile(9L,"male3","user6",null,profileColors));
                
        
        JavaRDD<TwUserProfile> twProfilesRdd = jsc.parallelize(twUserProfileLst);
        double[] predictionsDescr={0,1,1,2};
        double[] predictionsCols={0,1,0,0,0,0,0,0,0,0,0,0};
        GRA.GRAConfig graConf=new GRA.GRAConfig()
                .coloursClassifierModel(new MlModelTest(predictionsCols))
                .descrClassifierModel(new MlModelTest(predictionsDescr))
                .featureExtractor(new FeatureExtractorTest(4, jsc))
                .namesGenderMap(new NamesGenderMapTest())
                .numColorBitsMapping(9)
                .numColorsMapping(4)
                .trainingPath(BASE_PATH);
        
        GRA instance = new GRA(graConf, jsc);
        JavaRDD<Character> resultRDD = instance.waterfallGraEvaluation(twProfilesRdd).map(pg -> pg.getGender().toChar());
        List<Character> resLst=resultRDD.collect();
        List<Character> expResult = Arrays.asList(new Character[]{'m','m','m','f','f','x','m','f','m'});
        
        assertEquals(expResult, resLst);
    }
    
}
