package com.tilab.ca.sda.gra_consumer_stream;

import com.tilab.ca.sda.consumer.utils.stream.BusConsumerConnection;
import com.tilab.ca.sda.gra_consumer_dao.GraConsumerDao;
import com.tilab.ca.sda.gra_consumer_stream.mock.FeatureExtractorTest;
import com.tilab.ca.sda.gra_consumer_stream.mock.MlModelTest;
import com.tilab.ca.sda.gra_consumer_stream.mock.NamesGenderMapTest;
import com.tilab.ca.sda.gra_core.GenderTypes;
import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.gra_core.StatsGenderCount;
import com.tilab.ca.sda.gra_core.components.GRA;
import com.tilab.ca.sda.sda.model.keys.DateHtKey;
import com.tilab.ca.sda.sda.model.keys.GeoLocTruncTimeKey;
import com.tilab.ca.spark_test_lib.streaming.SparkStreamingTest;
import com.tilab.ca.spark_test_lib.streaming.annotations.SparkTestConfig;
import com.tilab.ca.spark_test_lib.streaming.interfaces.ExpectedOutputHandler;
import com.tilab.ca.spark_test_lib.streaming.utils.TestStreamUtils;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.junit.Test;
import static org.junit.Assert.*;
import scala.Tuple2;


@SparkTestConfig(appName = "GraStreamConsumerTest", master = "local[2]", batchDurationMillis = 30000, useManualClock = true)
public class GraStreamConsumerTest extends SparkStreamingTest{
    
    private final String BASE_PATH;

    public GraStreamConsumerTest() {
        super(GraStreamConsumerTest.class);
        String workingDir = System.getProperty("user.dir");
        BASE_PATH = String.format("%s#src#test#resources#",
                workingDir).replace("#", File.separator);
    }

    /**
     * Test of getUniqueProfilesDStream method, of class GraStreamConsumer.
     */
    @Test
    public void testUniqueProfilesDStream() throws Exception {
        System.out.println("getUniqueProfilesDStream");
        GRA gra = getSimpleGRA(jssc.sparkContext());
        $newTest().expectedOutputHandler(new ProfilesOuputHandler(10))
                .sparkStreamJob((jssc, eoh) -> {

                    final JavaDStream<String> rawTwDStream = TestStreamUtils.createMockDStreamFromDir(jssc, 1, BASE_PATH + File.separator +"data"+ File.separator + "profiles_tests");
                    JavaDStream<ProfileGender> resultUniqueProfilesDStream = GraStreamConsumer.getUniqueProfilesDStream(rawTwDStream, gra);
                    resultUniqueProfilesDStream.foreachRDD(resRdd -> {
                        ((ProfilesOuputHandler) eoh).resultProfiles.addAll(resRdd.collect());
                        return null;
                    });
                    JavaPairDStream<Long, GenderTypes> pairUidGenderResult = GraStreamConsumer.getUidGenderPairDStream(resultUniqueProfilesDStream);
                    pairUidGenderResult.foreachRDD(resPairRdd -> {
                        ((ProfilesOuputHandler) eoh).resultProfilesPair.addAll(resPairRdd.collect());
                        return null;
                    });

                })
                .test((eoh) -> {
                    ProfilesOuputHandler poh = ((ProfilesOuputHandler) eoh);
                    assertEquals(5, poh.resultProfiles.size());
                    Collections.sort(poh.resultProfiles, (pg1, pg2) -> ((Long) pg1.getTwProfile().getUid()).compareTo(pg2.getTwProfile().getUid()));
                   
                    assertEquals(1, poh.resultProfiles.get(0).getTwProfile().getUid());
                    assertEquals(GenderTypes.MALE, poh.resultProfiles.get(0).getGender());
                    assertEquals(2, poh.resultProfiles.get(1).getTwProfile().getUid());
                    assertEquals(GenderTypes.FEMALE, poh.resultProfiles.get(1).getGender());
                    assertEquals(3, poh.resultProfiles.get(2).getTwProfile().getUid());
                    assertEquals(GenderTypes.FEMALE, poh.resultProfiles.get(2).getGender());
                    assertEquals(4, poh.resultProfiles.get(3).getTwProfile().getUid());
                    assertEquals(GenderTypes.MALE, poh.resultProfiles.get(3).getGender());
                    assertEquals(5, poh.resultProfiles.get(4).getTwProfile().getUid());
                    assertEquals(GenderTypes.PAGE, poh.resultProfiles.get(4).getGender());
                    
                    assertEquals(5, poh.resultProfilesPair.size());
                    Collections.sort(poh.resultProfilesPair, (pg1, pg2) -> pg1._1.compareTo(pg2._1));
                   
                    assertEquals(1, poh.resultProfilesPair.get(0)._1.longValue());
                    assertEquals(GenderTypes.MALE, poh.resultProfilesPair.get(0)._2);
                    assertEquals(2, poh.resultProfilesPair.get(1)._1.longValue());
                    assertEquals(GenderTypes.FEMALE, poh.resultProfilesPair.get(1)._2);
                    assertEquals(3, poh.resultProfilesPair.get(2)._1.longValue());
                    assertEquals(GenderTypes.FEMALE, poh.resultProfilesPair.get(2)._2);
                    assertEquals(4, poh.resultProfilesPair.get(3)._1.longValue());
                    assertEquals(GenderTypes.MALE, poh.resultProfilesPair.get(3)._2);
                    assertEquals(5, poh.resultProfilesPair.get(4)._1.longValue());
                    assertEquals(GenderTypes.PAGE, poh.resultProfilesPair.get(4)._2);

                })
                .executeTest(2, 30000);
    }


    /**
     * Test of start method, of class GraStreamConsumer.
     
    @Test
    public void testStart() throws Exception {
        System.out.println("start");
        JavaStreamingContext jssc = null;
        GraStreamProperties graProps = null;
        GraConsumerDao graDao = null;
        BusConsumerConnection busConnection = null;
        String confsPath = "";
        GraStreamConsumer.start(jssc, graProps, graDao, busConnection, confsPath);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

   

    /**
     * Test of countGeoStatuses method, of class GraStreamConsumer.
     
    @Test
    public void testCountGeoStatuses() {
        System.out.println("countGeoStatuses");
        JavaDStream<String> rawTwDStreamWindow = null;
        GraStreamProperties graProps = null;
        JavaPairDStream<Long, GenderTypes> uidGenders = null;
        int roundType = 0;
        Integer granMin = null;
        JavaPairDStream<GeoLocTruncTimeKey, StatsGenderCount> expResult = null;
        JavaPairDStream<GeoLocTruncTimeKey, StatsGenderCount> result = GraStreamConsumer.countGeoStatuses(rawTwDStreamWindow, graProps, uidGenders, roundType, granMin);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of countHtsStatuses method, of class GraStreamConsumer.
    
    @Test
    public void testCountHtsStatuses() {
        System.out.println("countHtsStatuses");
        JavaDStream<String> rawTwDStreamWindow = null;
        GraStreamProperties graProps = null;
        JavaPairDStream<Long, GenderTypes> uidGenders = null;
        int roundType = 0;
        Integer granMin = null;
        JavaPairDStream<DateHtKey, StatsGenderCount> expResult = null;
        JavaPairDStream<DateHtKey, StatsGenderCount> result = GraStreamConsumer.countHtsStatuses(rawTwDStreamWindow, graProps, uidGenders, roundType, granMin);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    } */
    
    
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
    
    
    private class ProfilesOuputHandler implements ExpectedOutputHandler{

        public List<ProfileGender> resultProfiles=new LinkedList<>();
        public List<Tuple2<Long, GenderTypes>> resultProfilesPair=new LinkedList<>();

        private int expectedNumResults;
        
        public ProfilesOuputHandler(int expectedNumResults) {
            this.expectedNumResults=expectedNumResults;
        }
       
        @Override
        public boolean isExpectedOutputFilled() {
            return expectedNumResults == (resultProfiles.size()+resultProfilesPair.size());
        }
        
    }
}
