package com.tilab.ca.sda.consumer.tw.tot.core.test;

import com.tilab.ca.sda.consumer.tw.tot.core.TwCounter;
import com.tilab.ca.sda.consumer.tw.tot.core.data.DateHtKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.GeoLocTruncKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.GeoLocTruncTimeKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.StatsCounter;
import com.tilab.ca.sda.ctw.utils.RoundType;
import com.tilab.ca.sda.ctw.utils.Utils;
import com.tilab.ca.sda.sda.model.GeoStatus;
import com.tilab.ca.sda.sda.model.HtsStatus;
import com.tilab.ca.spark_test_lib.batch.SparkBatchTest;
import com.tilab.ca.spark_test_lib.streaming.annotations.SparkTestConfig;
import com.tilab.ca.spark_test_lib.streaming.interfaces.ExpectedOutputHandler;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.junit.Assert;
import org.junit.Test;
import scala.Tuple2;


@SparkTestConfig(appName = "GeoCountTestCase", master = "local[2]")
public class GeoCountTestCase extends SparkBatchTest implements Serializable{

    public GeoCountTestCase() {
        super(GeoCountTestCase.class);
    }
    
    
    @Test
    public void simpleTestCountBound() {
        ExpectedGeoOH goh=new ExpectedGeoOH(3);
        System.out.println("Starting Test simpleTestCountBound..");
        $newBatchTest().expectedOutputHandler(goh)
                .sparkJob((jsc,eoh)->{
                    int numSlots=4;
                    int deltaMinutes=20;
                    int slot=deltaMinutes/numSlots;
                    
                    ZonedDateTime to=ZonedDateTime.now();
                    ZonedDateTime from=to.minusMinutes(deltaMinutes);
                    ZonedDateTime curr=from;
                    int truncatePos=3;
                    List<GeoStatus> geoStatusList=new LinkedList<>();
                    geoStatusList.add(getGeoStatusFromParams(1, 1, 1.12345667, 2.23344557,Date.from(from.minusMinutes(10).toInstant()), false, false, truncatePos)); //before from
                    geoStatusList.add(getGeoStatusFromParams(2, 1, 1.12345467, 2.23344886,Date.from(from.toInstant()), false, false, truncatePos)); 
                    
                    curr=curr.plusMinutes(slot);
                    geoStatusList.add(getGeoStatusFromParams(3, 2, 1.12345667, 2.23365789,Date.from(curr.toInstant()), true, false, truncatePos)); //geo1
                    geoStatusList.add(getGeoStatusFromParams(4, 3, 1.12376586, 2.23354743,Date.from(curr.toInstant()), true, false, truncatePos)); //geo1
                    geoStatusList.add(getGeoStatusFromParams(5, 4, 1.12356758, 2.23373468,Date.from(curr.toInstant()), false, true, truncatePos)); //geo1
                    geoStatusList.add(getGeoStatusFromParams(6, 5, 1.12374564, 2.23375456,Date.from(curr.toInstant()), false, true, truncatePos)); //geo1
                    
                    curr=curr.plusMinutes(slot);
                    geoStatusList.add(getGeoStatusFromParams(7, 6, 1.12355555, 2.23577841,Date.from(curr.toInstant()), false, false, truncatePos)); //geo2 
                    
                    curr=curr.plusMinutes(slot);
                    geoStatusList.add(getGeoStatusFromParams(8, 7, 1.12555555, 2.23677841,Date.from(curr.toInstant()), false, false, truncatePos)); //geo3
                    geoStatusList.add(getGeoStatusFromParams(9, 8, 1.12597555, 2.23695783,Date.from(curr.toInstant()), false, false, truncatePos)); //geo3
                    geoStatusList.add(getGeoStatusFromParams(10, 7, 1.12587565, 2.2369286,Date.from(curr.toInstant()), false, false, truncatePos)); //geo3
                    geoStatusList.add(getGeoStatusFromParams(11, 9, 1.12564575, 2.23682726,Date.from(curr.plusSeconds(20).toInstant()), true, false, truncatePos)); //geo3
                    geoStatusList.add(getGeoStatusFromParams(12, 10,1.125493727, 2.23692836,Date.from(to.plusMinutes(10).toInstant()), false, false, truncatePos)); //geo3
               

                    JavaRDD<GeoStatus> geoStatusesRDD=jsc.parallelize(geoStatusList);
                   
                    
                    JavaPairRDD<GeoLocTruncKey, StatsCounter> geoStatusesFromTimeBoundsPairRDD= TwCounter.countGeoStatusesFromTimeBounds(geoStatusesRDD,from,to);
                    ((ExpectedGeoOH)eoh).setGeoCountOutputList(geoStatusesFromTimeBoundsPairRDD.collect());
                })
                .test((eoh) ->{
                    ExpectedGeoOH ego=(ExpectedGeoOH)eoh;
                    List<Tuple2<GeoLocTruncKey,StatsCounter>> gol=ego.getGeoCountOutputList();
                    Assert.assertNotNull(gol);
                    gol.sort((t1,t2) ->{
                       int comp=((Double)t1._1.getLatTrunc()).compareTo(t2._1.getLatTrunc());
                       if(comp==0)
                           return ((Double)t1._1.getLongTrunc()).compareTo(t2._1.getLongTrunc());        
                       return comp;
                    });
                    Assert.assertEquals(3,gol.size());
                    
                    Assert.assertEquals(1.123F,gol.get(0)._1.getLatTrunc(),3);
                    Assert.assertEquals(2.233F,gol.get(0)._1.getLongTrunc(),3);
                    
                    Assert.assertEquals(1,gol.get(0)._2.getNumTw());
                    Assert.assertEquals(2,gol.get(0)._2.getNumRtw());
                    Assert.assertEquals(2,gol.get(0)._2.getNumReply());
                    
                    Assert.assertEquals(1.123F,gol.get(1)._1.getLatTrunc(),3);
                    Assert.assertEquals(2.235F,gol.get(1)._1.getLongTrunc(),3);
                    
                    Assert.assertEquals(1,gol.get(1)._2.getNumTw());
                    Assert.assertEquals(0,gol.get(1)._2.getNumRtw());
                    Assert.assertEquals(0,gol.get(1)._2.getNumReply());
                    
                    Assert.assertEquals(1.125F,gol.get(2)._1.getLatTrunc(),3);
                    Assert.assertEquals(2.236F,gol.get(2)._1.getLongTrunc(),3);
                    
                    Assert.assertEquals(3,gol.get(2)._2.getNumTw());
                    Assert.assertEquals(1,gol.get(2)._2.getNumRtw());
                    Assert.assertEquals(0,gol.get(2)._2.getNumReply());   
                })
                .executeTest(30000);
        System.out.println("simpleTestCountBound END");
    }
    
    private Date dateFromStr(String dateStr){
        ZonedDateTime zDate=ZonedDateTime.parse(dateStr);
        return Date.from(zDate.toInstant());
    }
    
    
    @Test
    public void simpleTestCountRound() {
        HtCountTestCase.ExpectedHtsRound edh=new HtCountTestCase.ExpectedHtsRound(4);
       
        $newBatchTest().expectedOutputHandler(edh)
                .sparkJob((jsc,eoh)->{
                    int truncatePos=3;
                    
                    List<GeoStatus> geoStatusList=new LinkedList<>();
                    geoStatusList.add(getGeoStatusFromParams(1, 1, 1.1234, 2.2334,dateFromStr("2015-02-12T15:46:02+01:00"), false, false, truncatePos)); 
                    geoStatusList.add(getGeoStatusFromParams(2, 2, 1.1234, 2.2335,dateFromStr("2015-02-12T15:46:05+01:00"), true, false, truncatePos)); 
                    geoStatusList.add(getGeoStatusFromParams(3, 3, 1.1234, 2.2344,dateFromStr("2015-02-12T15:46:11+01:00"), false, false, truncatePos)); 
                    geoStatusList.add(getGeoStatusFromParams(4, 1, 1.1234, 2.2345,dateFromStr("2015-02-12T15:46:23+01:00"), false, false, truncatePos)); 
                    
                    geoStatusList.add(getGeoStatusFromParams(5, 4, 1.1234, 2.2334,dateFromStr("2015-02-12T15:59:59+01:00"), false, false, truncatePos)); 
                    geoStatusList.add(getGeoStatusFromParams(6, 2, 1.1234, 2.2335,dateFromStr("2015-02-12T15:59:14+01:00"), false, false, truncatePos)); 
                    geoStatusList.add(getGeoStatusFromParams(7, 1, 1.1234, 2.2334,dateFromStr("2015-02-12T15:59:27+01:00"), false, false, truncatePos)); 
                    geoStatusList.add(getGeoStatusFromParams(8, 3, 1.1234, 2.2334,dateFromStr("2015-02-12T15:59:34+01:00"), true, false, truncatePos)); 
                    geoStatusList.add(getGeoStatusFromParams(9, 5, 1.1234, 2.2334,dateFromStr("2015-02-12T15:59:35+01:00"), false, true, truncatePos)); 
                    
                    geoStatusList.add(getGeoStatusFromParams(10, 5, 1.1234, 2.2344,dateFromStr("2015-02-12T16:13:59+01:00"), false, false, truncatePos)); 
                    geoStatusList.add(getGeoStatusFromParams(11, 6, 1.1261, 2.2344,dateFromStr("2015-02-12T16:13:59+01:00"), false, false, truncatePos)); 
                    geoStatusList.add(getGeoStatusFromParams(12, 7, 1.1261, 2.2344,dateFromStr("2015-02-12T16:13:59+01:00"), true, false, truncatePos));
                    
                    
                    geoStatusList.add(getGeoStatusFromParams(13, 7, 1.1271, 2.2374,dateFromStr("2015-02-12T17:02:54+01:00"), true, false, truncatePos));
                    geoStatusList.add(getGeoStatusFromParams(14, 7, 1.1272, 2.2378,dateFromStr("2015-02-12T17:02:22+01:00"), true, false, truncatePos));
                    
                    
                    /*
                    JavaRDD<HtsStatus> htsStatuses=jsc.parallelize(htStatusList);
                    HtCountTestCase.ExpectedHtsRound ehr=((HtCountTestCase.ExpectedHtsRound)eoh);
                    JavaPairRDD<DateHtKey, StatsCounter> htsStatusesFromTimeBounds= TwCounter.countHtsStatuses(htsStatuses,RoundType.ROUND_TYPE_MIN,null);
                    ehr.addHtsCountOutputList(htsStatusesFromTimeBounds.collect());
                    
                    ehr.addHtsCountOutputList(TwCounter.countHtsStatuses(htsStatuses,RoundType.ROUND_TYPE_MIN,5).collect()); //group by 5 min

                    ehr.addHtsCountOutputList(TwCounter.countHtsStatuses(htsStatuses,RoundType.ROUND_TYPE_HOUR,null).collect()); //group by hour

                    ehr.addHtsCountOutputList(TwCounter.countHtsStatuses(htsStatuses,RoundType.ROUND_TYPE_DAY,null).collect()); //group by day 
                })
                .test((eoh) ->{
                    HtCountTestCase.ExpectedHtsRound ehr=((HtCountTestCase.ExpectedHtsRound)eoh);
                    
                    Assert.assertEquals(4,ehr.getHtsCountOutputList().size());
                    
                    //check round min
                    List<Tuple2<DateHtKey,StatsCounter>> roundMinOutputList=ehr.getHtsCountOutputList().get(0);
                    Assert.assertEquals(5,roundMinOutputList.size());
                    roundMinOutputList.sort((t1,t2) ->{
                       int comp=t1._1.getDate().compareTo(t2._1.getDate());
                       if(comp==0)
                           return t1._1.getHt().compareTo(t2._1.getHt());          
                       return comp;
                    });
                    
                    Assert.assertEquals("h1",roundMinOutputList.get(0)._1.getHt());
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T15:44:00+01:00").toInstant()),
                                        roundMinOutputList.get(0)._1.getDate());
                    Assert.assertEquals(2,roundMinOutputList.get(0)._2.getNumTw());
                    Assert.assertEquals(1,roundMinOutputList.get(0)._2.getNumRtw());
                    Assert.assertEquals(1,roundMinOutputList.get(0)._2.getNumReply());
                    
                    Assert.assertEquals("h1",roundMinOutputList.get(1)._1.getHt());
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T15:47:00+01:00").toInstant()),
                                        roundMinOutputList.get(1)._1.getDate());
                    Assert.assertEquals(1,roundMinOutputList.get(1)._2.getNumTw());
                    Assert.assertEquals(0,roundMinOutputList.get(1)._2.getNumRtw());
                    Assert.assertEquals(0,roundMinOutputList.get(1)._2.getNumReply());
                    
                    Assert.assertEquals("h2",roundMinOutputList.get(2)._1.getHt());
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T15:47:00+01:00").toInstant()),
                                        roundMinOutputList.get(2)._1.getDate());
                    Assert.assertEquals(0,roundMinOutputList.get(2)._2.getNumTw());
                    Assert.assertEquals(3,roundMinOutputList.get(2)._2.getNumRtw());
                    Assert.assertEquals(2,roundMinOutputList.get(2)._2.getNumReply());
                    
                    Assert.assertEquals("h1",roundMinOutputList.get(3)._1.getHt());
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T16:00:00+01:00").toInstant()),
                                        roundMinOutputList.get(3)._1.getDate());
                    Assert.assertEquals(0,roundMinOutputList.get(3)._2.getNumTw());
                    Assert.assertEquals(1,roundMinOutputList.get(3)._2.getNumRtw());
                    Assert.assertEquals(0,roundMinOutputList.get(3)._2.getNumReply());
                    
                    Assert.assertEquals("h3",roundMinOutputList.get(4)._1.getHt());
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T16:00:00+01:00").toInstant()),
                                        roundMinOutputList.get(4)._1.getDate());
                    Assert.assertEquals(2,roundMinOutputList.get(4)._2.getNumTw());
                    Assert.assertEquals(1,roundMinOutputList.get(4)._2.getNumRtw());
                    Assert.assertEquals(2,roundMinOutputList.get(4)._2.getNumReply());
                    
                    //TEST ON GRAN MIN
                    List<Tuple2<DateHtKey,StatsCounter>> roundMinGranOutputList=ehr.getHtsCountOutputList().get(1);
                    Assert.assertEquals(5,roundMinGranOutputList.size());
                    roundMinGranOutputList.sort((t1,t2) ->{
                       int comp=t1._1.getDate().compareTo(t2._1.getDate());
                       if(comp==0)
                           return t1._1.getHt().compareTo(t2._1.getHt());          
                       return comp;
                    });
                    
                    Assert.assertEquals("h1",roundMinGranOutputList.get(0)._1.getHt());
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T15:40:00+01:00").toInstant()),
                                        roundMinGranOutputList.get(0)._1.getDate());
                    Assert.assertEquals(2,roundMinGranOutputList.get(0)._2.getNumTw());
                    Assert.assertEquals(1,roundMinGranOutputList.get(0)._2.getNumRtw());
                    Assert.assertEquals(1,roundMinGranOutputList.get(0)._2.getNumReply());
                    
                    Assert.assertEquals("h1",roundMinGranOutputList.get(1)._1.getHt());
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T15:45:00+01:00").toInstant()),
                                        roundMinGranOutputList.get(1)._1.getDate());
                    Assert.assertEquals(1,roundMinGranOutputList.get(1)._2.getNumTw());
                    Assert.assertEquals(0,roundMinGranOutputList.get(1)._2.getNumRtw());
                    Assert.assertEquals(0,roundMinGranOutputList.get(1)._2.getNumReply());
                    
                    Assert.assertEquals("h2",roundMinGranOutputList.get(2)._1.getHt());
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T15:45:00+01:00").toInstant()),
                                        roundMinGranOutputList.get(2)._1.getDate());
                    Assert.assertEquals(0,roundMinGranOutputList.get(2)._2.getNumTw());
                    Assert.assertEquals(3,roundMinGranOutputList.get(2)._2.getNumRtw());
                    Assert.assertEquals(2,roundMinGranOutputList.get(2)._2.getNumReply());
                    
                    Assert.assertEquals("h1",roundMinGranOutputList.get(3)._1.getHt());
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T16:00:00+01:00").toInstant()),
                                        roundMinGranOutputList.get(3)._1.getDate());
                    Assert.assertEquals(0,roundMinGranOutputList.get(3)._2.getNumTw());
                    Assert.assertEquals(1,roundMinGranOutputList.get(3)._2.getNumRtw());
                    Assert.assertEquals(0,roundMinGranOutputList.get(3)._2.getNumReply());
                    
                    Assert.assertEquals("h3",roundMinGranOutputList.get(4)._1.getHt());
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T16:00:00+01:00").toInstant()),
                                        roundMinGranOutputList.get(4)._1.getDate());
                    Assert.assertEquals(2,roundMinGranOutputList.get(4)._2.getNumTw());
                    Assert.assertEquals(1,roundMinGranOutputList.get(4)._2.getNumRtw());
                    Assert.assertEquals(2,roundMinGranOutputList.get(4)._2.getNumReply());
                    
                    //TEST ON HOUR
                    List<Tuple2<DateHtKey,StatsCounter>> roundHourOutputList=ehr.getHtsCountOutputList().get(2);
                    Assert.assertEquals(4,roundHourOutputList.size());
                    roundHourOutputList.sort((t1,t2) ->{
                       int comp=t1._1.getDate().compareTo(t2._1.getDate());
                       if(comp==0)
                           return t1._1.getHt().compareTo(t2._1.getHt());          
                       return comp;
                    });
                    
                    Assert.assertEquals("h1",roundHourOutputList.get(0)._1.getHt());
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T15:00:00+01:00").toInstant()),
                                        roundHourOutputList.get(0)._1.getDate());
                    Assert.assertEquals(3,roundHourOutputList.get(0)._2.getNumTw());
                    Assert.assertEquals(1,roundHourOutputList.get(0)._2.getNumRtw());
                    Assert.assertEquals(1,roundHourOutputList.get(0)._2.getNumReply());
                    
                    Assert.assertEquals("h2",roundHourOutputList.get(1)._1.getHt());
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T15:00:00+01:00").toInstant()),
                                        roundHourOutputList.get(1)._1.getDate());
                    Assert.assertEquals(0,roundHourOutputList.get(1)._2.getNumTw());
                    Assert.assertEquals(3,roundHourOutputList.get(1)._2.getNumRtw());
                    Assert.assertEquals(2,roundHourOutputList.get(1)._2.getNumReply());
                    
                    Assert.assertEquals("h1",roundMinGranOutputList.get(3)._1.getHt());
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T16:00:00+01:00").toInstant()),
                                        roundHourOutputList.get(2)._1.getDate());
                    Assert.assertEquals(0,roundHourOutputList.get(2)._2.getNumTw());
                    Assert.assertEquals(1,roundHourOutputList.get(2)._2.getNumRtw());
                    Assert.assertEquals(0,roundHourOutputList.get(2)._2.getNumReply());
                    
                    Assert.assertEquals("h3",roundHourOutputList.get(3)._1.getHt());
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T16:00:00+01:00").toInstant()),
                                        roundHourOutputList.get(3)._1.getDate());
                    Assert.assertEquals(2,roundHourOutputList.get(3)._2.getNumTw());
                    Assert.assertEquals(1,roundHourOutputList.get(3)._2.getNumRtw());
                    Assert.assertEquals(2,roundHourOutputList.get(3)._2.getNumReply());
                    
                    
                    //TEST ON DAY
                    List<Tuple2<DateHtKey,StatsCounter>> roundDayOutputList=ehr.getHtsCountOutputList().get(3);
                    Assert.assertEquals(3,roundDayOutputList.size());
                    roundDayOutputList.sort((t1,t2) ->{
                       int comp=t1._1.getDate().compareTo(t2._1.getDate());
                       if(comp==0)
                           return t1._1.getHt().compareTo(t2._1.getHt());          
                       return comp;
                    });
                    
                    Assert.assertEquals("h1",roundDayOutputList.get(0)._1.getHt());
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T00:00:00+01:00").toInstant()),
                                        roundDayOutputList.get(0)._1.getDate());
                    Assert.assertEquals(3,roundDayOutputList.get(0)._2.getNumTw());
                    Assert.assertEquals(2,roundDayOutputList.get(0)._2.getNumRtw());
                    Assert.assertEquals(1,roundDayOutputList.get(0)._2.getNumReply());
                    
                    Assert.assertEquals("h2",roundDayOutputList.get(1)._1.getHt());
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T00:00:00+01:00").toInstant()),
                                        roundDayOutputList.get(1)._1.getDate());
                    Assert.assertEquals(0,roundDayOutputList.get(1)._2.getNumTw());
                    Assert.assertEquals(3,roundDayOutputList.get(1)._2.getNumRtw());
                    Assert.assertEquals(2,roundDayOutputList.get(1)._2.getNumReply());
                    
                    Assert.assertEquals("h3",roundDayOutputList.get(2)._1.getHt());
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T00:00:00+01:00").toInstant()),
                                        roundDayOutputList.get(2)._1.getDate());
                    Assert.assertEquals(2,roundDayOutputList.get(2)._2.getNumTw());
                    Assert.assertEquals(1,roundDayOutputList.get(2)._2.getNumRtw());
                    Assert.assertEquals(2,roundDayOutputList.get(2)._2.getNumReply());
                    */
                })
                .executeTest(30000);
                
    }
    
    
    private GeoStatus getGeoStatusFromParams(long postId,long userId,double latitude,double longitude,Date sentTime,boolean retweet,boolean reply,int truncPos){ 
        GeoStatus geoStatus=new GeoStatus();
        geoStatus.setLatTrunc(Utils.truncateDouble(latitude, truncPos));
        geoStatus.setLongTrunc(Utils.truncateDouble(longitude, truncPos));
        geoStatus.setLatitude(latitude);
        geoStatus.setLongitude(longitude);
        geoStatus.setSentTime(sentTime);
        geoStatus.setRetweet(retweet);
        geoStatus.setReply(reply);
        geoStatus.setPostId(postId);
        geoStatus.setUserId(userId);
  
        return geoStatus;
    }
    
    public static class ExpectedGeoOH implements ExpectedOutputHandler{

        private List<Tuple2<GeoLocTruncKey,StatsCounter>> geoCountOutputList;
        private final int expectedOutputSize;

        public ExpectedGeoOH(int expectedOutputSize) {
            this.expectedOutputSize=expectedOutputSize;
        }

        public List<Tuple2<GeoLocTruncKey, StatsCounter>> getGeoCountOutputList() {
            return geoCountOutputList;
        }

        public void setGeoCountOutputList(List<Tuple2<GeoLocTruncKey, StatsCounter>> geoCountOutputList) {
            this.geoCountOutputList = geoCountOutputList;
        }
        

        @Override
        public boolean isExpectedOutputFilled() {
            return geoCountOutputList.size()==expectedOutputSize; 
        }
    
    }
    
    public static class ExpectedGeoRound implements ExpectedOutputHandler{

        private List<List<Tuple2<GeoLocTruncTimeKey,StatsCounter>>> geoCountOutputListRound;
        private final int expectedOutputSize;

        public ExpectedGeoRound(int expectedOutputSize) {
            this.expectedOutputSize=expectedOutputSize;
        }

        public List<List<Tuple2<GeoLocTruncTimeKey, StatsCounter>>> getHtsCountOutputList() {
            return geoCountOutputListRound;
        }

        public void setHtsCountOutputList(List<List<Tuple2<GeoLocTruncTimeKey, StatsCounter>>> geoCountOutputListRound) {
            this.geoCountOutputListRound = geoCountOutputListRound;
        }
        
         public void addHtsCountOutputList(List<Tuple2<GeoLocTruncTimeKey, StatsCounter>> geoCountOutputListRound) {
            if(geoCountOutputListRound==null)
                geoCountOutputListRound=new LinkedList<>();
            this.geoCountOutputListRound.add(geoCountOutputListRound);
        }
        
        @Override
        public boolean isExpectedOutputFilled() {
            return geoCountOutputListRound.size()==expectedOutputSize; 
        }
    
    }
}
