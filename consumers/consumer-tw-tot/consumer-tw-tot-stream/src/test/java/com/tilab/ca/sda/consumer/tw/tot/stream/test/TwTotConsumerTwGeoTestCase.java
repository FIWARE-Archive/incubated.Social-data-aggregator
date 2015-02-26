package com.tilab.ca.sda.consumer.tw.tot.stream.test;

import com.tilab.ca.sda.consumer.tw.tot.core.data.DateHtKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.GeoLocTruncTimeKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.StatsCounter;
import com.tilab.ca.sda.consumer.tw.tot.stream.TotTwStreamConsumer;
import com.tilab.ca.sda.consumer.tw.tot.stream.TwTotConsumerProperties;
import com.tilab.ca.sda.consumer.tw.tot.stream.test.mocks.TwPropsTestImpl;
import com.tilab.ca.sda.consumer.utils.stream.BusConsumerConnection;
import com.tilab.ca.sda.ctw.utils.JsonUtils;
import com.tilab.ca.sda.ctw.utils.RoundType;
import com.tilab.ca.sda.ctw.utils.Utils;
import com.tilab.ca.sda.sda.model.GeoStatus;
import com.tilab.ca.spark_test_lib.streaming.SparkStreamingTest;
import com.tilab.ca.spark_test_lib.streaming.annotations.SparkTestConfig;
import com.tilab.ca.spark_test_lib.streaming.interfaces.ExpectedOutputHandler;
import com.tilab.ca.spark_test_lib.streaming.utils.TestStreamUtils;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.junit.Assert;
import org.junit.Test;
import scala.Tuple2;

@SparkTestConfig(appName = "TwTotStreamConsumerTest", master = "local[2]", batchDurationMillis = 30000, useManualClock = true)
public class TwTotConsumerTwGeoTestCase extends SparkStreamingTest implements Serializable{

    private final String BASE_PATH;
    
    public TwTotConsumerTwGeoTestCase() {
        super(TwTotConsumerTwGeoTestCase.class);
        String workingDir = System.getProperty("user.dir");
        BASE_PATH = String.format("%s#src#test#resources#data#",
                workingDir).replace("#", File.separator);
    }

    @Test
    public void testGeoCountStream() {
        $newTest()
            .expectedOutputHandler(new ExpectedGeoRound(4))
            .sparkStreamJob((jssc,eoh) ->{
                TwTotConsumerProperties twProps=new TwPropsTestImpl();
                
                //load data
                final JavaDStream<String> geoStatusDStream=TestStreamUtils.createMockDStreamFromDir(jssc, 1, BASE_PATH+File.separator+"geo_test_1");
                
               JavaPairDStream<GeoLocTruncTimeKey, StatsCounter> geoPairDStream = TotTwStreamConsumer.collectGeoStatus(twProps, new BusConsumerConnection() {

                    @Override
                    public void init(JavaStreamingContext jssc) {}

                    @Override
                    public JavaDStream<String> getDStreamByKey(String key) {
                        return geoStatusDStream;
                    }
                }, RoundType.ROUND_TYPE_MIN);
               
                ExpectedGeoRound egr =((ExpectedGeoRound)eoh);
                geoPairDStream.foreachRDD((rdd) -> {
                    egr.addGeoCountOutputListRound(rdd.collect());
                    return null;
                });
            })
            .test((eoh) ->{
                ExpectedGeoRound egr =((ExpectedGeoRound)eoh);
                List<Tuple2<GeoLocTruncTimeKey, StatsCounter>> geoCountOutputList=egr.getGeoCountOutputListRound();
                Assert.assertEquals(4,geoCountOutputList.size());
                geoCountOutputList.sort((t1,t2) -> {
                    int compDate=t1._1.getDate().compareTo(t2._1.getDate());
                    if(compDate==0){
                        int compLat=((Double)t1._1.getGeoLocTruncKey().getLatTrunc()).compareTo(t2._1.getGeoLocTruncKey().getLatTrunc());
                        if(compLat==0)
                             return ((Double)t1._1.getGeoLocTruncKey().getLongTrunc()).compareTo(t2._1.getGeoLocTruncKey().getLongTrunc());
                        return compLat;
                    }  
                    return compDate;
                });
                
                Assert.assertEquals(1.123,geoCountOutputList.get(0)._1.getGeoLocTruncKey().getLatTrunc(),3);
                Assert.assertEquals(4.567,geoCountOutputList.get(0)._1.getGeoLocTruncKey().getLongTrunc(),3);
                Assert.assertEquals(toDate("2015-02-12T17:00:00+01:00"),geoCountOutputList.get(0)._1.getDate());
                Assert.assertEquals(6,geoCountOutputList.get(0)._2.getNumTw());
                Assert.assertEquals(2,geoCountOutputList.get(0)._2.getNumRtw());
                Assert.assertEquals(4,geoCountOutputList.get(0)._2.getNumReply());
                
                Assert.assertEquals(1.125,geoCountOutputList.get(1)._1.getGeoLocTruncKey().getLatTrunc(),3);
                Assert.assertEquals(4.666,geoCountOutputList.get(1)._1.getGeoLocTruncKey().getLongTrunc(),3);
                Assert.assertEquals(toDate("2015-02-12T17:00:00+01:00"),geoCountOutputList.get(1)._1.getDate());
                Assert.assertEquals(0,geoCountOutputList.get(1)._2.getNumTw());
                Assert.assertEquals(0,geoCountOutputList.get(1)._2.getNumRtw());
                Assert.assertEquals(2,geoCountOutputList.get(1)._2.getNumReply());
                
                Assert.assertEquals(1.135,geoCountOutputList.get(2)._1.getGeoLocTruncKey().getLatTrunc(),3);
                Assert.assertEquals(4.894,geoCountOutputList.get(2)._1.getGeoLocTruncKey().getLongTrunc(),3);
                Assert.assertEquals(toDate("2015-02-12T17:00:00+01:00"),geoCountOutputList.get(2)._1.getDate());
                Assert.assertEquals(1,geoCountOutputList.get(2)._2.getNumTw());
                Assert.assertEquals(0,geoCountOutputList.get(2)._2.getNumRtw());
                Assert.assertEquals(0,geoCountOutputList.get(2)._2.getNumReply());
                
                Assert.assertEquals(1.135,geoCountOutputList.get(3)._1.getGeoLocTruncKey().getLatTrunc(),3);
                Assert.assertEquals(4.894,geoCountOutputList.get(3)._1.getGeoLocTruncKey().getLongTrunc(),3);
                Assert.assertEquals(toDate("2015-02-12T17:03:00+01:00"),geoCountOutputList.get(3)._1.getDate());
                Assert.assertEquals(4,geoCountOutputList.get(3)._2.getNumTw());
                Assert.assertEquals(1,geoCountOutputList.get(3)._2.getNumRtw());
                Assert.assertEquals(0,geoCountOutputList.get(3)._2.getNumReply());
                
                
            }).executeTest(20, 60000);
    }
    
    private Date toDate(String str) {
        return Date.from(ZonedDateTime.parse(str).toInstant());
    }

    
    public static class ExpectedGeoRound implements ExpectedOutputHandler {

        private List<Tuple2<GeoLocTruncTimeKey, StatsCounter>> geoCountOutputListRound=new LinkedList<>();
        private final int expectedOutputSize;

        public ExpectedGeoRound(int expectedOutputSize) {
            this.expectedOutputSize = expectedOutputSize;
        }

        public List<Tuple2<GeoLocTruncTimeKey, StatsCounter>> getGeoCountOutputListRound() {
            return geoCountOutputListRound;
        }

        public void setGeoCountOutputListRound(List<Tuple2<GeoLocTruncTimeKey, StatsCounter>> geoCountOutputListRound) {
            this.geoCountOutputListRound = geoCountOutputListRound;
        }

        public void addGeoCountOutputListRound(List<Tuple2<GeoLocTruncTimeKey, StatsCounter>> geoCountOutputListRound) {
            this.geoCountOutputListRound.addAll(geoCountOutputListRound);
        }

        @Override
        public boolean isExpectedOutputFilled() {
            return geoCountOutputListRound.size() == expectedOutputSize;
        }

    }
}
