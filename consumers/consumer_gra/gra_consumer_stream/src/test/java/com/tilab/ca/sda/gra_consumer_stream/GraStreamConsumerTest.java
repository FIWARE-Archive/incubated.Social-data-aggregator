package com.tilab.ca.sda.gra_consumer_stream;

import com.tilab.ca.sda.ctw.utils.RoundType;
import com.tilab.ca.sda.ctw.utils.Utils;
import com.tilab.ca.sda.gra_consumer_stream.mock.GRATestImpl;
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
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.junit.Test;
import static org.junit.Assert.*;
import scala.Tuple2;

@SparkTestConfig(appName = "GraStreamConsumerTest", master = "local[2]", batchDurationMillis = 30000, useManualClock = true)
public class GraStreamConsumerTest extends SparkStreamingTest {

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
        Map<Long, GenderTypes> graRespMap = new HashMap<>();
        graRespMap.put(1L, GenderTypes.MALE);
        graRespMap.put(2L, GenderTypes.FEMALE);
        graRespMap.put(3L, GenderTypes.FEMALE);
        graRespMap.put(4L, GenderTypes.MALE);
        graRespMap.put(5L, GenderTypes.PAGE);
        GRA gra = new GRATestImpl(graRespMap);
        $newTest().expectedOutputHandler(new ProfilesOuputHandler(10))
                .sparkStreamJob((jssc, eoh) -> {

                    final JavaDStream<String> rawTwDStream = TestStreamUtils.createMockDStreamFromDir(jssc, 1, BASE_PATH + File.separator + "data" + File.separator + "profiles_tests");
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
                .executeTest(1, 30000);
    }

    /**
     * Test of countGeoStatuses method, of class GraStreamConsumer.
     */
    @Test
    public void testCountGeoStatuses() {
        System.out.println("countGeoStatuses");
        int roundType = RoundType.ROUND_TYPE_MIN;
        int roundPos = 3;
        Integer granMin = 5;

        Map<Long, GenderTypes> graRespMap = new HashMap<>();
        graRespMap.put(1L, GenderTypes.MALE);
        graRespMap.put(2L, GenderTypes.MALE);
        graRespMap.put(3L, GenderTypes.FEMALE);
        graRespMap.put(4L, GenderTypes.FEMALE);
        graRespMap.put(5L, GenderTypes.PAGE);
        graRespMap.put(6L, GenderTypes.MALE);
        graRespMap.put(7L, GenderTypes.FEMALE);
        graRespMap.put(8L, GenderTypes.PAGE);
        graRespMap.put(9L, GenderTypes.PAGE);
        graRespMap.put(10L, GenderTypes.PAGE);

        GRA gra = new GRATestImpl(graRespMap);

        $newTest().expectedOutputHandler(new GenericOuputHandler<>(4))
                .sparkStreamJob((jssc, eoh) -> {
                    final JavaDStream<String> rawTwDStream = TestStreamUtils.createMockDStreamFromDir(jssc, 1, BASE_PATH + File.separator + "data" + File.separator + "geo_tests");
                    JavaDStream<ProfileGender> resultUniqueProfilesDStream = GraStreamConsumer.getUniqueProfilesDStream(rawTwDStream, gra);
                    JavaPairDStream<Long, GenderTypes> uidGenders = GraStreamConsumer.getUidGenderPairDStream(resultUniqueProfilesDStream);
                    JavaPairDStream<GeoLocTruncTimeKey, StatsGenderCount> resultRdd = GraStreamConsumer.countGeoStatuses(rawTwDStream,
                            uidGenders, roundPos,
                            roundType, granMin);
                    resultRdd.foreachRDD(geoRdd -> {
                        System.out.println("RESULTS ARE: -----------------------------------------------");
                        geoRdd.collect().forEach(geotuple -> System.out.println(geotuple._1.getDate()+" "+geotuple._1.getGeoLocTruncKey().getLatTrunc()));
                        ((GenericOuputHandler) eoh).result.addAll(geoRdd.collect());
                        return null;
                    });

                }).test(eoh -> {
                    GenericOuputHandler<GeoLocTruncTimeKey, StatsGenderCount> goh = ((GenericOuputHandler) eoh);
                    //sort for date crescent and latitude decrescent
                    Collections.sort(goh.result, (tp1,tp2) -> {
                        int cmp1 = tp1._1.getDate().compareTo(tp2._1.getDate());
                        if (cmp1 == 0) {   
                            return ((Double) tp2._1.getGeoLocTruncKey().getLatTrunc()).compareTo(tp1._1.getGeoLocTruncKey().getLatTrunc());
                        }
                        return cmp1;
                    });
                    String time1="2015-03-17T18:15:00+01:00";
                    assertEquals(4, goh.result.size());
                    assertEquals(Utils.Time.zonedDateTime2Date(ZonedDateTime.parse(time1)), goh.result.get(0)._1.getDate());
                    assertEquals(52.222f, goh.result.get(0)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    assertEquals(12.232f, goh.result.get(0)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    
                    assertEquals(2, goh.result.get(0)._2.getNumTwMales());
                    assertEquals(3, goh.result.get(0)._2.getNumTwFemales());
                    assertEquals(1, goh.result.get(0)._2.getNumRTwPages());
                    
                    assertEquals(Utils.Time.zonedDateTime2Date(ZonedDateTime.parse(time1)), goh.result.get(1)._1.getDate());
                    assertEquals(20.182f, goh.result.get(1)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    assertEquals(4.202f, goh.result.get(1)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    
                    assertEquals(1, goh.result.get(1)._2.getNumRTwMales());
                    assertEquals(2, goh.result.get(1)._2.getNumRplyFemales());
                    assertEquals(3, goh.result.get(1)._2.getNumTwPages());
                    
                    
                    String time2="2015-03-17T18:25:00+01:00";
                    assertEquals(Utils.Time.zonedDateTime2Date(ZonedDateTime.parse(time2)), goh.result.get(2)._1.getDate());
                    assertEquals(52.222f, goh.result.get(2)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    assertEquals(12.232f, goh.result.get(2)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    
                    assertEquals(3, goh.result.get(2)._2.getNumTwMales());
                    assertEquals(1, goh.result.get(2)._2.getNumRplyFemales());
                    assertEquals(1, goh.result.get(2)._2.getNumRTwPages());
                    
                    assertEquals(Utils.Time.zonedDateTime2Date(ZonedDateTime.parse(time2)), goh.result.get(3)._1.getDate());
                    assertEquals(7.222f, goh.result.get(3)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    assertEquals(18.222f, goh.result.get(3)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    
                    assertEquals(1, goh.result.get(3)._2.getNumRTwMales());
                                       
                }).executeTest(1, 30000);
    }
    
     /**
     * Test of countHtsStatuses method, of class GraStreamConsumer.
    */
    @Test
    public void testCountHtsStatuses() {
        System.out.println("countHtsStatuses");
        
        int roundType = RoundType.ROUND_TYPE_MIN;
        Integer granMin = 5;

        Map<Long, GenderTypes> graRespMap = new HashMap<>();
        graRespMap.put(1L, GenderTypes.MALE);
        graRespMap.put(2L, GenderTypes.FEMALE);
        graRespMap.put(3L, GenderTypes.FEMALE);
        graRespMap.put(4L, GenderTypes.PAGE);
        graRespMap.put(5L, GenderTypes.MALE);
        graRespMap.put(6L, GenderTypes.PAGE);
        graRespMap.put(7L, GenderTypes.MALE);
        graRespMap.put(8L, GenderTypes.FEMALE);
        graRespMap.put(9L, GenderTypes.FEMALE);
        graRespMap.put(10L, GenderTypes.PAGE);

        GRA gra = new GRATestImpl(graRespMap);

        $newTest().expectedOutputHandler(new GenericOuputHandler<>(3))
                .sparkStreamJob((jssc, eoh) -> {
                    final JavaDStream<String> rawTwDStream = TestStreamUtils.createMockDStreamFromDir(jssc, 1, BASE_PATH + File.separator + "data" + File.separator + "hts_tests");
                    JavaDStream<ProfileGender> resultUniqueProfilesDStream = GraStreamConsumer.getUniqueProfilesDStream(rawTwDStream, gra);
                    JavaPairDStream<Long, GenderTypes> uidGenders = GraStreamConsumer.getUidGenderPairDStream(resultUniqueProfilesDStream);
                    JavaPairDStream<DateHtKey, StatsGenderCount> resultRdd= GraStreamConsumer.countHtsStatuses(rawTwDStream,uidGenders,roundType, granMin);
                    resultRdd.foreachRDD(htRdd -> {
                        ((GenericOuputHandler) eoh).result.addAll(htRdd.collect());
                        return null;
                    });

                }).test(eoh -> {
                    GenericOuputHandler<DateHtKey, StatsGenderCount> goh = ((GenericOuputHandler) eoh);
                    //sort for date crescent and latitude decrescent
                    Collections.sort(goh.result, (tp1,tp2) -> {
                        int cmp1 = tp1._1.getDate().compareTo(tp2._1.getDate());
                        if (cmp1 == 0) {   
                            return tp1._1.getHt().compareTo(tp2._1.getHt());
                        }
                        return cmp1;
                    });
                    
                    String time1="2015-03-17T17:00:00+01:00";
                    assertEquals(3, goh.result.size());
                    assertEquals(Utils.Time.zonedDateTime2Date(ZonedDateTime.parse(time1)), goh.result.get(0)._1.getDate());
                    assertEquals("ht1", goh.result.get(0)._1.getHt());
                   
                    assertEquals(1, goh.result.get(0)._2.getNumTwMales());
                    assertEquals(1, goh.result.get(0)._2.getNumTwFemales());
                    assertEquals(1, goh.result.get(0)._2.getNumRplyFemales());
                    assertEquals(1, goh.result.get(0)._2.getNumTwPages());
                    
                    assertEquals(Utils.Time.zonedDateTime2Date(ZonedDateTime.parse(time1)), goh.result.get(1)._1.getDate());
                    assertEquals("ht2", goh.result.get(1)._1.getHt());
                    
                    assertEquals(2, goh.result.get(1)._2.getNumTwMales());
                    assertEquals(2, goh.result.get(1)._2.getNumRTwFemales());
                    assertEquals(1, goh.result.get(1)._2.getNumTwPages());
                    
                    
                    String time2="2015-03-17T17:15:00+01:00";
                    assertEquals(Utils.Time.zonedDateTime2Date(ZonedDateTime.parse(time2)), goh.result.get(2)._1.getDate());
                    assertEquals("ht2", goh.result.get(2)._1.getHt());
                    
                    assertEquals(1, goh.result.get(2)._2.getNumRTwMales());
                    assertEquals(2, goh.result.get(2)._2.getNumTwFemales());
                    assertEquals(1, goh.result.get(2)._2.getNumRplyPages());
                                       
                }).executeTest(1, 30000);
     } 

    /**
     * Test of start method, of class GraStreamConsumer.
     *
     * @Test public void testStart() throws Exception {
     * System.out.println("start"); JavaStreamingContext jssc = null;
     * GraStreamProperties graProps = null; GraConsumerDao graDao = null;
     * BusConsumerConnection busConnection = null; String confsPath = "";
     * GraStreamConsumer.start(jssc, graProps, graDao, busConnection,
     * confsPath); // TODO review the generated test code and remove the default
     * call to fail. fail("The test case is a prototype.");
    }
     */
   
     
    private class ProfilesOuputHandler implements ExpectedOutputHandler {

        public List<ProfileGender> resultProfiles = new LinkedList<>();
        public List<Tuple2<Long, GenderTypes>> resultProfilesPair = new LinkedList<>();

        private final int expectedNumResults;

        public ProfilesOuputHandler(int expectedNumResults) {
            this.expectedNumResults = expectedNumResults;
        }

        @Override
        public boolean isExpectedOutputFilled() {
            return expectedNumResults == (resultProfiles.size() + resultProfilesPair.size());
        }

    }

    private class GenericOuputHandler<T,K> implements ExpectedOutputHandler {

        public List<Tuple2<T, K>> result = new LinkedList<>();

        private final int expectedNumResults;

        public GenericOuputHandler(int expectedNumResults) {
            this.expectedNumResults = expectedNumResults;
        }

        @Override
        public boolean isExpectedOutputFilled() {
            return expectedNumResults == result.size();
        }

    }
}
