package com.tilab.ca.sda.consumer.tw.tot.stream.test;


import com.tilab.ca.sda.sda.model.keys.DateHtKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.StatsCounter;
import com.tilab.ca.sda.consumer.tw.tot.stream.TotTwStreamConsumer;
import com.tilab.ca.sda.consumer.tw.tot.stream.TwTotConsumerProperties;
import com.tilab.ca.sda.consumer.tw.tot.stream.test.mocks.TwPropsTestImpl;
import com.tilab.ca.sda.consumer.utils.stream.BusConsumerConnection;
import com.tilab.ca.sda.ctw.utils.RoundType;
import com.tilab.ca.spark_test_lib.streaming.SparkStreamingTest;
import com.tilab.ca.spark_test_lib.streaming.annotations.SparkTestConfig;
import com.tilab.ca.spark_test_lib.streaming.interfaces.ExpectedOutputHandler;
import com.tilab.ca.spark_test_lib.streaming.utils.TestStreamUtils;
import java.io.File;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.junit.Assert;
import org.junit.Test;
import scala.Tuple2;

@SparkTestConfig(appName = "TwTotStreamConsumerTest", master = "local[2]", batchDurationMillis = 30000, useManualClock = true)
public class TwTotStreamConsumerTestCase extends SparkStreamingTest implements Serializable {

    private final String BASE_PATH;

    public TwTotStreamConsumerTestCase() {
        super(TwTotStreamConsumerTestCase.class);
        String workingDir = System.getProperty("user.dir");
        BASE_PATH = String.format("%s#src#test#resources#data#",
                workingDir).replace("#", File.separator);
    }

    @Test
    public void testHtsCountStream() {
        $newTest()
            .expectedOutputHandler(new ExpectedHtsRound(4))
            .sparkStreamJob((jssc,eoh) ->{
                TwTotConsumerProperties twProps=new TwPropsTestImpl();
                
                //load data
                final JavaDStream<String> htsStatusDStream=TestStreamUtils.createMockDStreamFromDir(jssc, 1, BASE_PATH+File.separator+"hts_test_1");
                
               JavaPairDStream<DateHtKey, StatsCounter> htsPairDStream = TotTwStreamConsumer.collectHtsStatus(twProps, new BusConsumerConnection() {

                    @Override
                    public void init(JavaStreamingContext jssc) {}

                    @Override
                    public JavaDStream<String> getDStreamByKey(String key) {
                        return htsStatusDStream;
                    }
                }, RoundType.ROUND_TYPE_MIN);
               
                ExpectedHtsRound ehr =((ExpectedHtsRound)eoh);
                htsPairDStream.foreachRDD((rdd) -> {
                    ehr.addHtsCountOutputListElems(rdd.collect());
                    return null;
                });
            })
            .test((eoh) ->{
                ExpectedHtsRound ehr =((ExpectedHtsRound)eoh);
                List<Tuple2<DateHtKey, StatsCounter>> htsCountOutputList=ehr.getHtsCountOutputList();
                Assert.assertEquals(4,htsCountOutputList.size());
                htsCountOutputList.sort((t1,t2) -> {
                    int comp=t1._1.getDate().compareTo(t2._1.getDate());
                    if(comp==0)
                        return t1._1.getHt().compareTo(t2._1.getHt());
                    return comp;
                });
                Assert.assertEquals("ht1",htsCountOutputList.get(0)._1.getHt());
                Assert.assertEquals(toDate("2015-02-12T17:00:00+01:00"),htsCountOutputList.get(0)._1.getDate());
                Assert.assertEquals(3,htsCountOutputList.get(0)._2.getNumTw());
                Assert.assertEquals(1,htsCountOutputList.get(0)._2.getNumRtw());
                Assert.assertEquals(2,htsCountOutputList.get(0)._2.getNumReply());
                
                Assert.assertEquals("ht1",htsCountOutputList.get(1)._1.getHt());
                Assert.assertEquals(toDate("2015-02-12T17:01:00+01:00"),htsCountOutputList.get(1)._1.getDate());
                Assert.assertEquals(1,htsCountOutputList.get(1)._2.getNumTw());
                Assert.assertEquals(0,htsCountOutputList.get(1)._2.getNumRtw());
                Assert.assertEquals(0,htsCountOutputList.get(1)._2.getNumReply());
                
                Assert.assertEquals("ht2",htsCountOutputList.get(2)._1.getHt());
                Assert.assertEquals(toDate("2015-02-12T17:01:00+01:00"),htsCountOutputList.get(2)._1.getDate());
                Assert.assertEquals(5,htsCountOutputList.get(2)._2.getNumTw());
                Assert.assertEquals(1,htsCountOutputList.get(2)._2.getNumRtw());
                Assert.assertEquals(1,htsCountOutputList.get(2)._2.getNumReply());
                
                Assert.assertEquals("ht3",htsCountOutputList.get(3)._1.getHt());
                Assert.assertEquals(toDate("2015-02-12T17:04:00+01:00"),htsCountOutputList.get(3)._1.getDate());
                Assert.assertEquals(1,htsCountOutputList.get(3)._2.getNumTw());
                Assert.assertEquals(1,htsCountOutputList.get(3)._2.getNumRtw());
                Assert.assertEquals(0,htsCountOutputList.get(3)._2.getNumReply());
                
            }).executeTest(20, 20000);
    }

    private Date toDate(String str) {
        return Date.from(ZonedDateTime.parse(str).toInstant());
    }

    public static class ExpectedHtsRound implements ExpectedOutputHandler {

        private List<Tuple2<DateHtKey, StatsCounter>> htsCountOutputList=new LinkedList<>();
        private final int expectedOutputSize;

        public ExpectedHtsRound(int expectedOutputSize) {
            this.expectedOutputSize = expectedOutputSize;
        }

        public List<Tuple2<DateHtKey, StatsCounter>> getHtsCountOutputList() {
            return htsCountOutputList;
        }

        public void setHtsCountOutputList(List<Tuple2<DateHtKey, StatsCounter>> htsCountOutputList) {
            this.htsCountOutputList = htsCountOutputList;
        }
        
        public void addHtsCountOutputListElems(List<Tuple2<DateHtKey, StatsCounter>> htsCountOutputList) {
            this.htsCountOutputList.addAll(htsCountOutputList);
        }

        @Override
        public boolean isExpectedOutputFilled() {
            return htsCountOutputList.size() == expectedOutputSize;
        }
    }
}
