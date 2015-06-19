package com.tilab.ca.sda.gra_consumer_batch;

import com.tilab.ca.sda.consumer.utils.BatchUtils;
import com.tilab.ca.sda.ctw.utils.RoundType;
import com.tilab.ca.sda.ctw.utils.Utils;
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
import java.time.ZonedDateTime;
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
import scala.Tuple2;


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
                .featureExtractor(new FeatureExtractorTest(3, jsc))
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
        assertEquals(GenderTypes.MALE, result.get(0).getGender());
        assertEquals(2, result.get(1).getTwProfile().getUid());
        assertEquals(GenderTypes.MALE, result.get(1).getGender());
        assertEquals(3, result.get(2).getTwProfile().getUid());
        assertEquals(GenderTypes.FEMALE, result.get(2).getGender());
        assertEquals(4, result.get(3).getTwProfile().getUid());
        assertEquals(GenderTypes.FEMALE, result.get(3).getGender());
        assertEquals(5, result.get(4).getTwProfile().getUid());
        assertEquals(GenderTypes.PAGE, result.get(4).getGender());
        assertEquals(6, result.get(5).getTwProfile().getUid());
        assertEquals(GenderTypes.PAGE, result.get(5).getGender());
        assertEquals(7, result.get(6).getTwProfile().getUid());
        assertEquals(GenderTypes.PAGE, result.get(6).getGender());
        
        System.out.println("fromProfileGenderToUserIdGenderPairRdd");
        JavaPairRDD<Long, GenderTypes> resPairRdd = GraEvaluateAndCount.fromProfileGenderToUserIdGenderPairRdd(resultRDD);
        List<Tuple2<Long,GenderTypes>> resPair=resPairRdd.collect();
        Collections.sort(resPair, (p1,p2)-> p1._1.compareTo(p2._1));
        
        assertEquals(7, resPair.size());
        assertEquals(1, (long)resPair.get(0)._1);
        assertEquals(GenderTypes.MALE, resPair.get(0)._2);
        assertEquals(2, (long)resPair.get(1)._1);
        assertEquals(GenderTypes.MALE, resPair.get(1)._2);
        assertEquals(3, (long)resPair.get(2)._1);
        assertEquals(GenderTypes.FEMALE, resPair.get(2)._2);
        assertEquals(4, (long)resPair.get(3)._1);
        assertEquals(GenderTypes.FEMALE, resPair.get(3)._2);
        assertEquals(5, (long)resPair.get(4)._1);
        assertEquals(GenderTypes.PAGE, resPair.get(4)._2);
        assertEquals(6, (long)resPair.get(5)._1);
        assertEquals(GenderTypes.PAGE, resPair.get(5)._2);
        assertEquals(7, (long)resPair.get(6)._1);
        assertEquals(GenderTypes.PAGE, resPair.get(6)._2);
    }

    /**
     * Test of countGeoStatuses method, of class GraEvaluateAndCount.
     */
    @Test
    public void testCountGeoStatuses() throws Exception {
        System.out.println("countGeoStatuses");
        
        //init gra
        double[] predictionsDescr={};
        double[] predictionsCols={};
        GRA.GRAConfig graConf=new GRA.GRAConfig()
                .coloursClassifierModel(new MlModelTest(predictionsCols))
                .descrClassifierModel(new MlModelTest(predictionsDescr))
                .featureExtractor(new FeatureExtractorTest(0, jsc))
                .namesGenderMap(new NamesGenderMapTest())
                .numColorBitsMapping(9)
                .numColorsMapping(4)
                .trainingPath(BASE_PATH);
        
        List<String> tweetsLst=new ArrayList<>();
        tweetsLst.add("{\"createdAt\":\"2014-07-08T23:16:19+02\",\"id\":1,\"inReplyToStatusId\":-1,\"geoLocation\":{\"latitude\":48.82956499,\"longitude\":2.29986439},\"hashtagEntities\":[],\"user\":{\"id\":1,\"name\":\"male1\",\"screenName\":\"john\",\"description\":\"descr\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T23:17:19+02\",\"id\":2,\"inReplyToStatusId\":-1,\"geoLocation\":{\"latitude\":48.82956499,\"longitude\":2.29986439},\"hashtagEntities\":[],\"user\":{\"id\":2,\"name\":\"male2\",\"screenName\":\"zach\",\"description\":\"descr\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T23:18:19+02\",\"id\":3,\"inReplyToStatusId\":-1,\"geoLocation\":{\"latitude\":48.82956499,\"longitude\":2.29986439},\"hashtagEntities\":[],\"user\":{\"id\":1,\"name\":\"male1\",\"screenName\":\"john\",\"description\":\"descr\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T23:16:19+02\",\"id\":4,\"inReplyToStatusId\":-1,\"geoLocation\":{\"latitude\":48.82956499,\"longitude\":2.29986439},\"hashtagEntities\":[],\"user\":{\"id\":3,\"name\":\"female1\",\"screenName\":\"deborah\",\"description\":\"descr\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T23:17:19+02\",\"id\":5,\"inReplyToStatusId\":-1,\"geoLocation\":{\"latitude\":48.82956499,\"longitude\":2.29986439},\"hashtagEntities\":[],\"user\":{\"id\":4,\"name\":\"female2\",\"screenName\":\"anne\",\"description\":\"descr\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T23:20:19+02\",\"id\":6,\"inReplyToStatusId\":-1,\"geoLocation\":{\"latitude\":49.80256499,\"longitude\":2.31986439},\"hashtagEntities\":[],\"user\":{\"id\":5,\"name\":\"page1\",\"screenName\":\"news_today\",\"description\":\"descr\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T23:21:19+02\",\"id\":7,\"inReplyToStatusId\":-1,\"geoLocation\":{\"latitude\":49.80256499,\"longitude\":2.31986439},\"hashtagEntities\":[],\"user\":{\"id\":6,\"name\":\"page2\",\"screenName\":\"official_page\",\"description\":\"descr\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T23:22:19+02\",\"id\":8,\"inReplyToStatusId\":-1,\"geoLocation\":{\"latitude\":49.80256499,\"longitude\":2.31986439},\"hashtagEntities\":[],\"user\":{\"id\":7,\"name\":\"male3\",\"screenName\":\"ben\",\"description\":\"descr\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        JavaRDD<String> tweetsRdd = jsc.parallelize(tweetsLst);
        GRA gra = new GRA(graConf, jsc);
        JavaRDD<ProfileGender> resultprofileRDD = GraEvaluateAndCount.evaluateUniqueProfilesRdd(tweetsRdd, gra);
        
        System.out.println("profiles are :---------------------------------------");
        resultprofileRDD.collect().forEach(p -> System.out.println(p.getTwProfile().getUid()+","+p.getGender().toChar()));
        
        JavaPairRDD<Long, GenderTypes> resPairRdd = GraEvaluateAndCount.fromProfileGenderToUserIdGenderPairRdd(resultprofileRDD);
        
        //geo conf and data
        JavaRDD<GeoStatus> geoStatuses = tweetsRdd.map(twStr -> BatchUtils.fromJstring2GeoStatus(twStr,3));
        
        int roundType = RoundType.ROUND_TYPE_MIN;
        Integer granMin = 5;
        
        JavaPairRDD<GeoLocTruncTimeKey, StatsGenderCount> resultRDD = GraEvaluateAndCount.countGeoStatuses(geoStatuses, resPairRdd, roundType, granMin);
        List<Tuple2<GeoLocTruncTimeKey, StatsGenderCount>> resultsTp2=resultRDD.collect();
        
        Collections.sort(resultsTp2, (tp1,tp2)-> tp1._1.getDate().compareTo(tp2._1.getDate()));
        
        System.out.println("---------------------------------------");
        resultsTp2.forEach(res -> System.out.println(res._1.getDate().toString()+","+res._2.getNumTwMales()+","+
                res._2.getNumTwFemales()+","+res._2.getNumTwPages()+","+res._2.getNumTwUndefined()));
        
        
        assertEquals(2, resultsTp2.size());
        assertEquals(Utils.Time.zonedDateTime2Date(ZonedDateTime.parse("2014-07-08T23:15:00+02:00")), resultsTp2.get(0)._1.getDate());
        assertEquals(48.829f, resultsTp2.get(0)._1.getGeoLocTruncKey().getLatTrunc(),3);
        assertEquals(2.299f, resultsTp2.get(0)._1.getGeoLocTruncKey().getLongTrunc(),3);
        assertEquals("expected 3 male profiles for time 2014-07-08T23:15:00+02:00",3, resultsTp2.get(0)._2.getNumTwMales());
        assertEquals("expected 2 female profiles for time 2014-07-08T23:15:00+02:00",2, resultsTp2.get(0)._2.getNumTwFemales());
        
        assertEquals(Utils.Time.zonedDateTime2Date(ZonedDateTime.parse("2014-07-08T23:20:00+02:00")), resultsTp2.get(1)._1.getDate());
        assertEquals(49.802f, resultsTp2.get(1)._1.getGeoLocTruncKey().getLatTrunc(),3);
        assertEquals(2.319f, resultsTp2.get(1)._1.getGeoLocTruncKey().getLongTrunc(),3);
        assertEquals("expected 1 male profile for time 2014-07-08T23:20:00+02:00",1, resultsTp2.get(1)._2.getNumTwMales());
        assertEquals("expected 2 pages profiles for time 2014-07-08T23:20:00+02:00",2, resultsTp2.get(1)._2.getNumTwPages());
        
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
