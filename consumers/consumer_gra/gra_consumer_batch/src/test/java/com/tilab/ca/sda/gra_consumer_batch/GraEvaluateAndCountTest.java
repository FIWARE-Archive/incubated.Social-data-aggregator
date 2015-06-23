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
import org.junit.Before;
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
    public void testGraGeoStatuses() throws Exception {
        System.out.println("countGeoStatuses");   
        GRA gra = getSimpleGRA(jsc);
        
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
        
        JavaRDD<ProfileGender> resultprofileRDD = GraEvaluateAndCount.evaluateUniqueProfilesRdd(tweetsRdd, gra);
        
        JavaPairRDD<Long, GenderTypes> resPairRdd = GraEvaluateAndCount.fromProfileGenderToUserIdGenderPairRdd(resultprofileRDD);
        
        //geo conf and data
        JavaRDD<GeoStatus> geoStatuses = tweetsRdd.map(twStr -> BatchUtils.fromJstring2GeoStatus(twStr,3));
        
        int roundType = RoundType.ROUND_TYPE_MIN;
        Integer granMin = 5;
        testCountGeoStatuses(geoStatuses, resPairRdd, roundType, granMin);
        testCountGeoStatusesFromTimeBounds(geoStatuses, resPairRdd);
    }
    
    public void testCountGeoStatuses(JavaRDD<GeoStatus> geoStatuses,JavaPairRDD<Long, GenderTypes> resPairRdd,int roundType,Integer granMin){
        System.out.println("testCountGeoStatuses");   
        JavaPairRDD<GeoLocTruncTimeKey, StatsGenderCount> resultRDD = GraEvaluateAndCount.countGeoStatuses(geoStatuses, resPairRdd, roundType, granMin);
        List<Tuple2<GeoLocTruncTimeKey, StatsGenderCount>> resultsTp2=resultRDD.collect();
        
        Collections.sort(resultsTp2, (tp1,tp2)-> tp1._1.getDate().compareTo(tp2._1.getDate()));
        
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
    public void testCountGeoStatusesFromTimeBounds(JavaRDD<GeoStatus> geoStatuses,JavaPairRDD<Long, GenderTypes> uidGenders) {
        System.out.println("countGeoStatusesFromTimeBounds");
        
        JavaPairRDD<GeoLocTruncKey, StatsGenderCount> resultRDD = GraEvaluateAndCount.countGeoStatusesFromTimeBounds(geoStatuses, uidGenders);
        List<Tuple2<GeoLocTruncKey, StatsGenderCount>> resultsTp2=resultRDD.collect();
        Collections.sort(resultsTp2, (tp1,tp2)-> {
           int cmp1=((Double)tp1._1.getLatTrunc()).compareTo(tp2._1.getLatTrunc());
           if(cmp1==0)
               return ((Double)tp1._1.getLongTrunc()).compareTo(tp2._1.getLongTrunc());
           return cmp1;
        });
        
        assertEquals(2, resultsTp2.size());
        assertEquals(48.829f, resultsTp2.get(0)._1.getLatTrunc(),3);
        assertEquals(2.299f, resultsTp2.get(0)._1.getLongTrunc(),3);
        assertEquals("expected 3 male profile for lt 48.829 lng 2.299",3, resultsTp2.get(0)._2.getNumTwMales());
        assertEquals("expected 2 female profile for lt 48.829 lng 2.299",2, resultsTp2.get(0)._2.getNumTwFemales());
        
        assertEquals(49.802f, resultsTp2.get(1)._1.getLatTrunc(),3);
        assertEquals(2.319f, resultsTp2.get(1)._1.getLongTrunc(),3);
        assertEquals("expected 1 male profile for lt 49.802 lng 2.319",1, resultsTp2.get(1)._2.getNumTwMales());
        assertEquals("expected 2 pages profile for lt 49.802 lng 2.319",2, resultsTp2.get(1)._2.getNumTwPages());
    }

    /**
     * Test of countHtsStatuses method, of class GraEvaluateAndCount.
     */
    @Test
    public void testGraHtsStatuses() throws Exception {
        System.out.println("countHtsStatuses");
        GRA gra = getSimpleGRA(jsc);
      
        List<String> tweetsLst=new ArrayList<>();
        tweetsLst.add("{\"createdAt\":\"2014-07-08T10:16:19+02\",\"id\":1,\"inReplyToStatusId\":-1,\"hashtagEntities\":[{\"text\":\"ht1\",\"start\":0,\"end\":19},{\"text\":\"ht2\",\"start\":0,\"end\":19}],\"user\":{\"id\":1,\"name\":\"male1\",\"screenName\":\"john\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T10:17:19+02\",\"id\":2,\"inReplyToStatusId\":-1,\"hashtagEntities\":[{\"text\":\"ht1\",\"start\":0,\"end\":19}],\"user\":{\"id\":1,\"name\":\"male1\",\"screenName\":\"john\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T10:16:30+02\",\"id\":3,\"inReplyToStatusId\":-1,\"hashtagEntities\":[{\"text\":\"ht1\",\"start\":0,\"end\":19}],\"user\":{\"id\":2,\"name\":\"pg1\",\"screenName\":\"news_g1\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T10:16:42+02\",\"id\":4,\"inReplyToStatusId\":-1,\"hashtagEntities\":[{\"text\":\"ht1\",\"start\":0,\"end\":19}],\"user\":{\"id\":3,\"name\":\"pg2\",\"screenName\":\"news_g2\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T10:16:53+02\",\"id\":5,\"inReplyToStatusId\":-1,\"hashtagEntities\":[{\"text\":\"ht1\",\"start\":0,\"end\":19}],\"user\":{\"id\":4,\"name\":\"pg3\",\"screenName\":\"news_g3\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T10:17:19+02\",\"id\":6,\"inReplyToStatusId\":-1,\"retweetedStatus\":{},\"hashtagEntities\":[{\"text\":\"ht1\",\"start\":0,\"end\":19}],\"user\":{\"id\":5,\"name\":\"female1\",\"screenName\":\"deborah\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T10:18:23+02\",\"id\":7,\"inReplyToStatusId\":1221323,\"hashtagEntities\":[{\"text\":\"ht1\",\"start\":0,\"end\":19}],\"user\":{\"id\":5,\"name\":\"female1\",\"screenName\":\"deborah\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T10:18:44+02\",\"id\":8,\"inReplyToStatusId\":1331231,\"hashtagEntities\":[{\"text\":\"ht1\",\"start\":0,\"end\":19}],\"user\":{\"id\":6,\"name\":\"female2\",\"screenName\":\"anne\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T10:15:57+02\",\"id\":9,\"inReplyToStatusId\":-1,\"hashtagEntities\":[{\"text\":\"ht2\",\"start\":0,\"end\":19}],\"user\":{\"id\":7,\"name\":\"female3\",\"screenName\":\"julie\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T10:16:19+02\",\"id\":10,\"inReplyToStatusId\":-1,\"retweetedStatus\":{},\"hashtagEntities\":[{\"text\":\"ht2\",\"start\":0,\"end\":19}],\"user\":{\"id\":8,\"name\":\"pg4\",\"screenName\":\"news_g4\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T10:16:28+02\",\"id\":11,\"inReplyToStatusId\":-1,\"retweetedStatus\":{},\"hashtagEntities\":[{\"text\":\"ht2\",\"start\":0,\"end\":19}],\"user\":{\"id\":9,\"name\":\"pg5\",\"screenName\":\"news_g5\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T10:20:01+02\",\"id\":12,\"inReplyToStatusId\":-1,\"hashtagEntities\":[{\"text\":\"ht1\",\"start\":0,\"end\":19}],\"user\":{\"id\":1,\"name\":\"male1\",\"screenName\":\"john\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T10:21:19+02\",\"id\":13,\"inReplyToStatusId\":-1,\"retweetedStatus\":{},\"hashtagEntities\":[{\"text\":\"ht1\",\"start\":0,\"end\":19}],\"user\":{\"id\":10,\"name\":\"male2\",\"screenName\":\"matt\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T10:22:19+02\",\"id\":14,\"inReplyToStatusId\":-1,\"retweetedStatus\":{},\"hashtagEntities\":[{\"text\":\"ht1\",\"start\":0,\"end\":19}],\"user\":{\"id\":1,\"name\":\"male1\",\"screenName\":\"john\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T10:23:12+02\",\"id\":15,\"inReplyToStatusId\":156231,\"hashtagEntities\":[{\"text\":\"ht1\",\"start\":0,\"end\":19}],\"user\":{\"id\":5,\"name\":\"female1\",\"screenName\":\"deborah\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T10:23:15+02\",\"id\":16,\"inReplyToStatusId\":131231,\"hashtagEntities\":[{\"text\":\"ht1\",\"start\":0,\"end\":19}],\"user\":{\"id\":6,\"name\":\"female2\",\"screenName\":\"anne\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        tweetsLst.add("{\"createdAt\":\"2014-07-08T10:24:58+02\",\"id\":17,\"inReplyToStatusId\":121332,\"hashtagEntities\":[{\"text\":\"ht1\",\"start\":0,\"end\":19}],\"user\":{\"id\":5,\"name\":\"female1\",\"screenName\":\"deborah\",\"profileBackgroundColor\":\"9AE4E8\",\"profileTextColor\":\"333333\",\"profileLinkColor\":\"0084B4\",\"profileSidebarFillColor\":\"DDFFCC\",\"profileSidebarBorderColor\":\"BDDCAD\"}}");
        
        JavaRDD<String> tweetsRdd = jsc.parallelize(tweetsLst);
        
        JavaRDD<ProfileGender> resultprofileRDD = GraEvaluateAndCount.evaluateUniqueProfilesRdd(tweetsRdd, gra);
        
        JavaPairRDD<Long, GenderTypes> uidGenders = GraEvaluateAndCount.fromProfileGenderToUserIdGenderPairRdd(resultprofileRDD);
        
        JavaRDD<HtsStatus> htsStatuses = tweetsRdd.flatMap(BatchUtils::fromJstring2HtsStatus);
        int roundType = RoundType.ROUND_TYPE_MIN;
        Integer granMin = 5;
        
        testCountHtsStatuses(htsStatuses, uidGenders,roundType,granMin);
        testCountHtsStatusesFromTimeBounds(htsStatuses, uidGenders);
    }

    public void testCountHtsStatuses(final JavaRDD<HtsStatus> htsStatuses,final JavaPairRDD<Long, GenderTypes> uidGenders,int roundType,int granMin) {
        System.out.println("testCountHtsStatuses");
        JavaPairRDD<DateHtKey, StatsGenderCount> resultRDD = GraEvaluateAndCount.countHtsStatuses(htsStatuses, uidGenders, roundType, granMin);
        List<Tuple2<DateHtKey, StatsGenderCount>> resultsTp2= resultRDD.collect();
        Collections.sort(resultsTp2, (tp1,tp2)-> {
            int cp1=tp1._1.getDate().compareTo(tp2._1.getDate());
            if(cp1==0)
                return tp1._1.getHt().compareTo(tp2._1.getHt());
            return cp1;
        });
        
        assertEquals(3, resultsTp2.size());
        assertEquals(Utils.Time.zonedDateTime2Date(ZonedDateTime.parse("2014-07-08T10:15:00+02:00")), resultsTp2.get(0)._1.getDate());
        assertEquals("ht1", resultsTp2.get(0)._1.getHt());
        assertEquals(2, resultsTp2.get(0)._2.getNumTwMales());
        assertEquals(3, resultsTp2.get(0)._2.getNumTwPages());
        assertEquals(0, resultsTp2.get(0)._2.getNumTwFemales());
        assertEquals(0, resultsTp2.get(0)._2.getNumTwUnknown());
        
        assertEquals(0, resultsTp2.get(0)._2.getNumRTwMales());
        assertEquals(0, resultsTp2.get(0)._2.getNumRTwPages());
        assertEquals(1, resultsTp2.get(0)._2.getNumRTwFemales());
        assertEquals(0, resultsTp2.get(0)._2.getNumRTwUnknown());
        
        assertEquals(0, resultsTp2.get(0)._2.getNumRplyMales());
        assertEquals(0, resultsTp2.get(0)._2.getNumRplyPages());
        assertEquals(2, resultsTp2.get(0)._2.getNumRplyFemales());
        assertEquals(0, resultsTp2.get(0)._2.getNumRplyUnknown());
        
        assertEquals(Utils.Time.zonedDateTime2Date(ZonedDateTime.parse("2014-07-08T10:15:00+02:00")), resultsTp2.get(1)._1.getDate());
        assertEquals("ht2", resultsTp2.get(1)._1.getHt());
        
        assertEquals(1, resultsTp2.get(1)._2.getNumTwMales());
        assertEquals(0, resultsTp2.get(1)._2.getNumTwPages());
        assertEquals(1, resultsTp2.get(1)._2.getNumTwFemales());
        assertEquals(0, resultsTp2.get(1)._2.getNumTwUnknown());
        
        assertEquals(0, resultsTp2.get(1)._2.getNumRTwMales());
        assertEquals(2, resultsTp2.get(1)._2.getNumRTwPages());
        assertEquals(0, resultsTp2.get(1)._2.getNumRTwFemales());
        assertEquals(0, resultsTp2.get(1)._2.getNumRTwUnknown());
        
        assertEquals(0, resultsTp2.get(1)._2.getNumRplyMales());
        assertEquals(0, resultsTp2.get(1)._2.getNumRplyPages());
        assertEquals(0, resultsTp2.get(1)._2.getNumRplyFemales());
        assertEquals(0, resultsTp2.get(1)._2.getNumRplyUnknown());
        
        assertEquals(Utils.Time.zonedDateTime2Date(ZonedDateTime.parse("2014-07-08T10:20:00+02:00")), resultsTp2.get(2)._1.getDate());
        assertEquals("ht1", resultsTp2.get(2)._1.getHt());
        
        assertEquals(1, resultsTp2.get(2)._2.getNumTwMales());
        assertEquals(0, resultsTp2.get(2)._2.getNumTwPages());
        assertEquals(0, resultsTp2.get(2)._2.getNumTwFemales());
        assertEquals(0, resultsTp2.get(2)._2.getNumTwUnknown());
        
        assertEquals(2, resultsTp2.get(2)._2.getNumRTwMales());
        assertEquals(0, resultsTp2.get(2)._2.getNumRTwPages());
        assertEquals(0, resultsTp2.get(2)._2.getNumRTwFemales());
        assertEquals(0, resultsTp2.get(2)._2.getNumRTwUnknown());
        
        assertEquals(0, resultsTp2.get(2)._2.getNumRplyMales());
        assertEquals(0, resultsTp2.get(2)._2.getNumRplyPages());
        assertEquals(3, resultsTp2.get(2)._2.getNumRplyFemales());
        assertEquals(0, resultsTp2.get(2)._2.getNumRplyUnknown());
    }
    
    /**
     * Test of countHtsStatusesFromTimeBounds method, of class GraEvaluateAndCount.
     */
    public void testCountHtsStatusesFromTimeBounds(final JavaRDD<HtsStatus> htsStatuses,final JavaPairRDD<Long, GenderTypes> uidGenders) {
        System.out.println("countHtsStatusesFromTimeBounds"); 
        JavaPairRDD<String, StatsGenderCount> result = GraEvaluateAndCount.countHtsStatusesFromTimeBounds(htsStatuses, uidGenders);
        List<Tuple2<String, StatsGenderCount>> resultsTp2=result.collect();
        
        
        Collections.sort(resultsTp2, (tp1,tp2)-> tp1._1.compareTo(tp2._1));
        
        assertEquals(2, resultsTp2.size());
        assertEquals("ht1", resultsTp2.get(0)._1);
        
        assertEquals(3, resultsTp2.get(0)._2.getNumTwMales());
        assertEquals(3, resultsTp2.get(0)._2.getNumTwPages());
        assertEquals(0, resultsTp2.get(0)._2.getNumTwFemales());
        assertEquals(0, resultsTp2.get(0)._2.getNumTwUnknown());
        
        assertEquals(2, resultsTp2.get(0)._2.getNumRTwMales());
        assertEquals(0, resultsTp2.get(0)._2.getNumRTwPages());
        assertEquals(1, resultsTp2.get(0)._2.getNumRTwFemales());
        assertEquals(0, resultsTp2.get(0)._2.getNumRTwUnknown());
        
        assertEquals(0, resultsTp2.get(0)._2.getNumRplyMales());
        assertEquals(0, resultsTp2.get(0)._2.getNumRplyPages());
        assertEquals(5, resultsTp2.get(0)._2.getNumRplyFemales());
        assertEquals(0, resultsTp2.get(0)._2.getNumRplyUnknown());
        
        assertEquals("ht2", resultsTp2.get(1)._1);
        
        assertEquals(1, resultsTp2.get(1)._2.getNumTwMales());
        assertEquals(0, resultsTp2.get(1)._2.getNumTwPages());
        assertEquals(1, resultsTp2.get(1)._2.getNumTwFemales());
        assertEquals(0, resultsTp2.get(1)._2.getNumTwUnknown());
        
        assertEquals(0, resultsTp2.get(1)._2.getNumRTwMales());
        assertEquals(2, resultsTp2.get(1)._2.getNumRTwPages());
        assertEquals(0, resultsTp2.get(1)._2.getNumRTwFemales());
        assertEquals(0, resultsTp2.get(1)._2.getNumRTwUnknown());
        
        assertEquals(0, resultsTp2.get(1)._2.getNumRplyMales());
        assertEquals(0, resultsTp2.get(1)._2.getNumRplyPages());
        assertEquals(0, resultsTp2.get(1)._2.getNumRplyFemales());
        assertEquals(0, resultsTp2.get(1)._2.getNumRplyUnknown());
    }
    
    private GRA getSimpleGRA(JavaSparkContext jsc) throws Exception{
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
        return new GRA(graConf, jsc);
    }
    
}
