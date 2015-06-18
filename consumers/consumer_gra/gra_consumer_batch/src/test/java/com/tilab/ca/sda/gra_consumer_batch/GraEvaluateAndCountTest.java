package com.tilab.ca.sda.gra_consumer_batch;

import com.tilab.ca.sda.gra_consumer_batch.mock.FeatureExtractorTest;
import com.tilab.ca.sda.gra_consumer_batch.mock.MlModelTest;
import com.tilab.ca.sda.gra_consumer_batch.mock.NamesGenderMapTest;
import com.tilab.ca.sda.gra_core.GenderTypes;
import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.gra_core.StatsGenderCount;
import com.tilab.ca.sda.gra_core.components.GRA;
import com.tilab.ca.sda.sda.model.GeoStatus;
import com.tilab.ca.sda.sda.model.HtsStatus;
import com.tilab.ca.sda.sda.model.keys.DateHtKey;
import com.tilab.ca.sda.sda.model.keys.GeoLocTruncKey;
import com.tilab.ca.sda.sda.model.keys.GeoLocTruncTimeKey;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


public class GraEvaluateAndCountTest {
    
    private final String BASE_PATH;
    private JavaSparkContext jsc;
    
    public GraEvaluateAndCountTest() {
        String workingDir = System.getProperty("user.dir");
        
        BASE_PATH = String.format("%s#src#test#resources#",
                workingDir).replace("#", File.separator);
    }
    
    @Before
    public void setUp() {
        jsc=new JavaSparkContext("local", "testGRABatch");
    }
    
    @After
    public void tearDown() {
         jsc.stop();
    }

    /**
     * Test of evaluateUniqueProfilesRdd method, of class GraEvaluateAndCount.
     */
    @Test
    public void testEvaluateUniqueProfilesRdd() throws Exception {
        System.out.println("evaluateUniqueProfilesRdd");
        
        double[] predictionsDescr={2,2,2};
        double[] predictionsCols={1,1};
        GRA.GRAConfig graConf=new GRA.GRAConfig()
                .coloursClassifierModel(new MlModelTest(predictionsCols))
                .descrClassifierModel(new MlModelTest(predictionsDescr))
                .featureExtractor(new FeatureExtractorTest(4, jsc))
                .namesGenderMap(new NamesGenderMapTest())
                .numColorBitsMapping(9)
                .numColorsMapping(4)
                .trainingPath(BASE_PATH);
        
        List<String> tweetsLst=new ArrayList<>();
        tweetsLst.add("{\"createdAt\":\"2015-03-17T19:04:29+01\",\"id\":1,\"text\":\"text1\",\"user\":{\"id\":1,\"name\":\"Zach Miller\",\"screenName\":\"zachmiller\",\"description\":\"my descr\",\"profileBackgroundColor\":\"030A09\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"4460A6\",\"profileSidebarFillColor\":\"FFFFFF\",\"profileSidebarBorderColor\":\"000000\"}}");
        tweetsLst.add("{\"createdAt\":\"2015-03-17T19:04:29+01\",\"id\":2,\"text\":\"text2\",\"user\":{\"id\":2,\"name\":\"John Snow\",\"screenName\":\"js\",\"description\":\"my descr\",\"profileBackgroundColor\":\"030A09\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"4460A6\",\"profileSidebarFillColor\":\"FFFFFF\",\"profileSidebarBorderColor\":\"000000\"}}");
        tweetsLst.add("{\"createdAt\":\"2015-03-17T19:05:29+01\",\"id\":3,\"text\":\"text3\",\"user\":{\"id\":1,\"name\":\"Zach Miller\",\"screenName\":\"zachmiller\",\"description\":\"my descr\",\"profileBackgroundColor\":\"030A09\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"4460A6\",\"profileSidebarFillColor\":\"FFFFFF\",\"profileSidebarBorderColor\":\"000000\"}}");
        tweetsLst.add("{\"createdAt\":\"2015-03-17T19:04:29+01\",\"id\":4,\"text\":\"text4\",\"user\":{\"id\":3,\"name\":\"sheuser\",\"screenName\":\"sheuser\",\"description\":\"\",\"profileBackgroundColor\":\"030A09\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"4460A6\",\"profileSidebarFillColor\":\"FFFFFF\",\"profileSidebarBorderColor\":\"000000\"}}");
        tweetsLst.add("{\"createdAt\":\"2015-03-17T19:04:29+01\",\"id\":5,\"text\":\"text5\",\"user\":{\"id\":4,\"name\":\"anotherFemale\",\"screenName\":\"anotherfemale\",\"profileBackgroundColor\":\"030A09\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"4460A6\",\"profileSidebarFillColor\":\"FFFFFF\",\"profileSidebarBorderColor\":\"000000\"}}");
        tweetsLst.add("{\"createdAt\":\"2015-03-17T19:04:29+01\",\"id\":6,\"text\":\"text6\",\"user\":{\"id\":5,\"name\":\"page1\",\"screenName\":\"page1\",\"description\":\"my descr\",\"profileBackgroundColor\":\"030A09\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"4460A6\",\"profileSidebarFillColor\":\"FFFFFF\",\"profileSidebarBorderColor\":\"000000\"}}");
        tweetsLst.add("{\"createdAt\":\"2015-03-17T19:04:29+01\",\"id\":7,\"text\":\"text7\",\"user\":{\"id\":6,\"name\":\"page2\",\"screenName\":\"page2\",\"description\":\"my descr\",\"profileBackgroundColor\":\"030A09\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"4460A6\",\"profileSidebarFillColor\":\"FFFFFF\",\"profileSidebarBorderColor\":\"000000\"}}");
        tweetsLst.add("{\"createdAt\":\"2015-03-17T19:04:29+01\",\"id\":8,\"text\":\"text7\",\"user\":{\"id\":7,\"name\":\"page3\",\"screenName\":\"page3\",\"description\":\"my descr\",\"profileBackgroundColor\":\"030A09\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"4460A6\",\"profileSidebarFillColor\":\"FFFFFF\",\"profileSidebarBorderColor\":\"000000\"}}");
        JavaRDD<String> tweetsRdd = jsc.parallelize(tweetsLst);
        GRA gra = new GRA(graConf, jsc);
        JavaRDD<ProfileGender> resultRDD = GraEvaluateAndCount.evaluateUniqueProfilesRdd(tweetsRdd, gra);
        List<ProfileGender> result=resultRDD.collect();
        
        Collections.sort(result, (pg1,pg2)-> ((Long)pg1.getTwProfile().getUid()).compareTo(pg2.getTwProfile().getUid()));
        
        assertEquals(7, result.size());
        assertEquals(1, result.get(0).getTwProfile().getUid());
        assertEquals(GenderTypes.MALE, result.get(0).getTwProfile().getUid());
        assertEquals(2, result.get(1).getTwProfile().getUid());
        assertEquals(GenderTypes.MALE, result.get(1).getTwProfile().getUid());
        assertEquals(3, result.get(2).getTwProfile().getUid());
        assertEquals(GenderTypes.FEMALE, result.get(2).getTwProfile().getUid());
        assertEquals(4, result.get(3).getTwProfile().getUid());
        assertEquals(GenderTypes.FEMALE, result.get(3).getTwProfile().getUid());
        assertEquals(5, result.get(4).getTwProfile().getUid());
        assertEquals(GenderTypes.PAGE, result.get(4).getTwProfile().getUid());
        assertEquals(6, result.get(5).getTwProfile().getUid());
        assertEquals(GenderTypes.PAGE, result.get(5).getTwProfile().getUid());
        assertEquals(7, result.get(6).getTwProfile().getUid());
        assertEquals(GenderTypes.PAGE, result.get(6).getTwProfile().getUid());    
    }

    /**
     * Test of fromProfileGenderToUserIdGenderPairRdd method, of class GraEvaluateAndCount.
     */
    @Test
    public void testFromProfileGenderToUserIdGenderPairRdd() {
        /*
        System.out.println("fromProfileGenderToUserIdGenderPairRdd");
        JavaRDD<ProfileGender> profilesGenderRdd = null;
        JavaPairRDD<Long, GenderTypes> expResult = null;
        JavaPairRDD<Long, GenderTypes> result = GraEvaluateAndCount.fromProfileGenderToUserIdGenderPairRdd(profilesGenderRdd);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");*/
    }

    /**
     * Test of countGeoStatuses method, of class GraEvaluateAndCount.
     */
    @Test
    public void testCountGeoStatuses() {
        /*
        System.out.println("countGeoStatuses");
        JavaRDD<GeoStatus> geoStatuses = null;
        JavaPairRDD<Long, GenderTypes> uidGenders = null;
        int roundType = 0;
        Integer granMin = null;
        JavaPairRDD<GeoLocTruncTimeKey, StatsGenderCount> expResult = null;
        JavaPairRDD<GeoLocTruncTimeKey, StatsGenderCount> result = GraEvaluateAndCount.countGeoStatuses(geoStatuses, uidGenders, roundType, granMin);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");*/
    }

    /**
     * Test of countGeoStatusesFromTimeBounds method, of class GraEvaluateAndCount.
     */
    @Test
    public void testCountGeoStatusesFromTimeBounds() {
        /*
        System.out.println("countGeoStatusesFromTimeBounds");
        JavaRDD<GeoStatus> geoStatuses = null;
        JavaPairRDD<Long, GenderTypes> uidGenders = null;
        JavaPairRDD<GeoLocTruncKey, StatsGenderCount> expResult = null;
        JavaPairRDD<GeoLocTruncKey, StatsGenderCount> result = GraEvaluateAndCount.countGeoStatusesFromTimeBounds(geoStatuses, uidGenders);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");*/
    }

    /**
     * Test of countHtsStatuses method, of class GraEvaluateAndCount.
     */
    @Test
    public void testCountHtsStatuses() {
        /*
        System.out.println("countHtsStatuses");
        JavaRDD<HtsStatus> htsStatuses = null;
        JavaPairRDD<Long, GenderTypes> uidGenders = null;
        int roundType = 0;
        Integer granMin = null;
        JavaPairRDD<DateHtKey, StatsGenderCount> expResult = null;
        JavaPairRDD<DateHtKey, StatsGenderCount> result = GraEvaluateAndCount.countHtsStatuses(htsStatuses, uidGenders, roundType, granMin);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");*/
    }

    /**
     * Test of countHtsStatusesFromTimeBounds method, of class GraEvaluateAndCount.
     */
    @Test
    public void testCountHtsStatusesFromTimeBounds() {
        /*
        System.out.println("countHtsStatusesFromTimeBounds");
        JavaRDD<HtsStatus> htsStatuses = null;
        JavaPairRDD<Long, GenderTypes> uidGenders = null;
        JavaPairRDD<String, StatsGenderCount> expResult = null;
        JavaPairRDD<String, StatsGenderCount> result = GraEvaluateAndCount.countHtsStatusesFromTimeBounds(htsStatuses, uidGenders);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");*/
    }
    
}
