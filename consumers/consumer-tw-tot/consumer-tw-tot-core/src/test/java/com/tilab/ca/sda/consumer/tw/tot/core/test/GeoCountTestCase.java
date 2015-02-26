package com.tilab.ca.sda.consumer.tw.tot.core.test;

import com.tilab.ca.sda.consumer.tw.tot.core.TwCounter;
import com.tilab.ca.sda.consumer.tw.tot.core.data.GeoLocTruncKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.GeoLocTruncTimeKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.StatsCounter;
import com.tilab.ca.sda.ctw.utils.RoundType;
import com.tilab.ca.sda.ctw.utils.Utils;
import com.tilab.ca.sda.sda.model.GeoStatus;
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
        ExpectedGeoRound edh=new ExpectedGeoRound(4);
       
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
                    
                    
                    geoStatusList.add(getGeoStatusFromParams(13, 7, 1.1271, 2.2374,dateFromStr("2015-02-12T17:02:54+01:00"), false, false, truncatePos));
                    geoStatusList.add(getGeoStatusFromParams(14, 7, 1.1272, 2.2378,dateFromStr("2015-02-12T17:02:22+01:00"), false, true, truncatePos));
                    
                    
                    JavaRDD<GeoStatus> geoStatusesRDD=jsc.parallelize(geoStatusList);
                    ExpectedGeoRound egr=(ExpectedGeoRound)eoh;
                    egr.addGeoCountOutputListRound(TwCounter.countGeoStatuses(geoStatusesRDD, RoundType.ROUND_TYPE_MIN, null).collect());
                    egr.addGeoCountOutputListRound(TwCounter.countGeoStatuses(geoStatusesRDD, RoundType.ROUND_TYPE_MIN, 3).collect());
                    egr.addGeoCountOutputListRound(TwCounter.countGeoStatuses(geoStatusesRDD, RoundType.ROUND_TYPE_HOUR, null).collect());
                    egr.addGeoCountOutputListRound(TwCounter.countGeoStatuses(geoStatusesRDD, RoundType.ROUND_TYPE_DAY, null).collect());
                    
                })
                .test((eoh) ->{
                    ExpectedGeoRound egr=(ExpectedGeoRound)eoh;
                    Assert.assertEquals(4,egr.getGeoCountOutputListRound().size());
                    
                    //check round min
                    List<Tuple2<GeoLocTruncTimeKey,StatsCounter>> roundMinOutputList=egr.getGeoCountOutputListRound().get(0);
                    Assert.assertEquals(6,roundMinOutputList.size());
                    sortGeoTimeList(roundMinOutputList);
                    Assert.assertEquals(1.123F,roundMinOutputList.get(0)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.233F,roundMinOutputList.get(0)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T15:46:00+01:00").toInstant()),
                                        roundMinOutputList.get(0)._1.getDate());
                    Assert.assertEquals(1,roundMinOutputList.get(0)._2.getNumTw());
                    Assert.assertEquals(1,roundMinOutputList.get(0)._2.getNumRtw());
                    Assert.assertEquals(0,roundMinOutputList.get(0)._2.getNumReply());
                   
                    Assert.assertEquals(1.123F,roundMinOutputList.get(1)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.234F,roundMinOutputList.get(1)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T15:46:00+01:00").toInstant()),
                                        roundMinOutputList.get(1)._1.getDate());
                    Assert.assertEquals(2,roundMinOutputList.get(1)._2.getNumTw());
                    Assert.assertEquals(0,roundMinOutputList.get(1)._2.getNumRtw());
                    Assert.assertEquals(0,roundMinOutputList.get(1)._2.getNumReply());
                    
                    Assert.assertEquals(1.123F,roundMinOutputList.get(2)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.233F,roundMinOutputList.get(2)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T15:59:00+01:00").toInstant()),
                                        roundMinOutputList.get(2)._1.getDate());
                    Assert.assertEquals(3,roundMinOutputList.get(2)._2.getNumTw());
                    Assert.assertEquals(1,roundMinOutputList.get(2)._2.getNumRtw());
                    Assert.assertEquals(1,roundMinOutputList.get(2)._2.getNumReply());
                    
                    Assert.assertEquals(1.123F,roundMinOutputList.get(3)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.234F,roundMinOutputList.get(3)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T16:13:00+01:00").toInstant()),
                                        roundMinOutputList.get(3)._1.getDate());
                    Assert.assertEquals(1,roundMinOutputList.get(3)._2.getNumTw());
                    Assert.assertEquals(0,roundMinOutputList.get(3)._2.getNumRtw());
                    Assert.assertEquals(0,roundMinOutputList.get(3)._2.getNumReply());
                    
                    Assert.assertEquals(1.126F,roundMinOutputList.get(4)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.234F,roundMinOutputList.get(4)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T16:13:00+01:00").toInstant()),
                                        roundMinOutputList.get(4)._1.getDate());
                    Assert.assertEquals(1,roundMinOutputList.get(4)._2.getNumTw());
                    Assert.assertEquals(1,roundMinOutputList.get(4)._2.getNumRtw());
                    Assert.assertEquals(0,roundMinOutputList.get(4)._2.getNumReply());
                    
                    Assert.assertEquals(1.127F,roundMinOutputList.get(5)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.237F,roundMinOutputList.get(5)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T17:02:00+01:00").toInstant()),
                                        roundMinOutputList.get(5)._1.getDate());
                    Assert.assertEquals(1,roundMinOutputList.get(5)._2.getNumTw());
                    Assert.assertEquals(0,roundMinOutputList.get(5)._2.getNumRtw());
                    Assert.assertEquals(1,roundMinOutputList.get(5)._2.getNumReply());
                    
                    //TEST ON GRAN MIN
                    
                    List<Tuple2<GeoLocTruncTimeKey,StatsCounter>> roundGranMinOutputList=egr.getGeoCountOutputListRound().get(1);
                    Assert.assertEquals(6,roundGranMinOutputList.size());
                    sortGeoTimeList(roundGranMinOutputList);
                    
                    Assert.assertEquals(1.123F,roundGranMinOutputList.get(0)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.233F,roundGranMinOutputList.get(0)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T15:45:00+01:00").toInstant()),
                                        roundGranMinOutputList.get(0)._1.getDate());
                    Assert.assertEquals(1,roundGranMinOutputList.get(0)._2.getNumTw());
                    Assert.assertEquals(1,roundGranMinOutputList.get(0)._2.getNumRtw());
                    Assert.assertEquals(0,roundGranMinOutputList.get(0)._2.getNumReply());
                   
                    Assert.assertEquals(1.123F,roundGranMinOutputList.get(1)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.234F,roundGranMinOutputList.get(1)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T15:45:00+01:00").toInstant()),
                                        roundGranMinOutputList.get(1)._1.getDate());
                    Assert.assertEquals(2,roundGranMinOutputList.get(1)._2.getNumTw());
                    Assert.assertEquals(0,roundGranMinOutputList.get(1)._2.getNumRtw());
                    Assert.assertEquals(0,roundGranMinOutputList.get(1)._2.getNumReply());
                    
                    Assert.assertEquals(1.123F,roundGranMinOutputList.get(2)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.233F,roundGranMinOutputList.get(2)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T15:57:00+01:00").toInstant()),
                                        roundGranMinOutputList.get(2)._1.getDate());
                    Assert.assertEquals(3,roundGranMinOutputList.get(2)._2.getNumTw());
                    Assert.assertEquals(1,roundGranMinOutputList.get(2)._2.getNumRtw());
                    Assert.assertEquals(1,roundGranMinOutputList.get(2)._2.getNumReply());
                    
                    Assert.assertEquals(1.123F,roundGranMinOutputList.get(3)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.234F,roundGranMinOutputList.get(3)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T16:12:00+01:00").toInstant()),
                                        roundGranMinOutputList.get(3)._1.getDate());
                    Assert.assertEquals(1,roundGranMinOutputList.get(3)._2.getNumTw());
                    Assert.assertEquals(0,roundGranMinOutputList.get(3)._2.getNumRtw());
                    Assert.assertEquals(0,roundGranMinOutputList.get(3)._2.getNumReply());
                    
                    Assert.assertEquals(1.126F,roundGranMinOutputList.get(4)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.234F,roundGranMinOutputList.get(4)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T16:12:00+01:00").toInstant()),
                                        roundGranMinOutputList.get(4)._1.getDate());
                    Assert.assertEquals(1,roundGranMinOutputList.get(4)._2.getNumTw());
                    Assert.assertEquals(1,roundGranMinOutputList.get(4)._2.getNumRtw());
                    Assert.assertEquals(0,roundGranMinOutputList.get(4)._2.getNumReply());
                    
                    Assert.assertEquals(1.126F,roundGranMinOutputList.get(5)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.234F,roundGranMinOutputList.get(5)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T17:00:00+01:00").toInstant()),
                                        roundGranMinOutputList.get(5)._1.getDate());
                    Assert.assertEquals(1,roundGranMinOutputList.get(5)._2.getNumTw());
                    Assert.assertEquals(0,roundGranMinOutputList.get(5)._2.getNumRtw());
                    Assert.assertEquals(1,roundGranMinOutputList.get(5)._2.getNumReply());
                    
                   
                    
                    //TEST ON ROUND HOUR
                    List<Tuple2<GeoLocTruncTimeKey,StatsCounter>> roundHourOutputList=egr.getGeoCountOutputListRound().get(2);
                    Assert.assertEquals(5,roundHourOutputList.size());
                    sortGeoTimeList(roundHourOutputList);
                    
                    Assert.assertEquals(1.123F,roundHourOutputList.get(0)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.233F,roundHourOutputList.get(0)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T15:00:00+01:00").toInstant()),
                                        roundHourOutputList.get(0)._1.getDate());
                    Assert.assertEquals(4,roundHourOutputList.get(0)._2.getNumTw());
                    Assert.assertEquals(2,roundHourOutputList.get(0)._2.getNumRtw());
                    Assert.assertEquals(1,roundHourOutputList.get(0)._2.getNumReply());
                    
                    
                    Assert.assertEquals(1.123F,roundHourOutputList.get(1)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.234F,roundHourOutputList.get(1)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T15:00:00+01:00").toInstant()),
                                        roundHourOutputList.get(1)._1.getDate());
                    Assert.assertEquals(2,roundHourOutputList.get(1)._2.getNumTw());
                    Assert.assertEquals(0,roundHourOutputList.get(1)._2.getNumRtw());
                    Assert.assertEquals(0,roundHourOutputList.get(1)._2.getNumReply());
                    
                    Assert.assertEquals(1.123F,roundHourOutputList.get(2)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.234F,roundHourOutputList.get(2)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T16:00:00+01:00").toInstant()),
                                        roundHourOutputList.get(2)._1.getDate());
                    Assert.assertEquals(1,roundHourOutputList.get(2)._2.getNumTw());
                    Assert.assertEquals(0,roundHourOutputList.get(2)._2.getNumRtw());
                    Assert.assertEquals(0,roundHourOutputList.get(2)._2.getNumReply());
                    
                    Assert.assertEquals(1.126F,roundHourOutputList.get(3)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.234F,roundHourOutputList.get(3)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T16:00:00+01:00").toInstant()),
                                        roundHourOutputList.get(3)._1.getDate());
                    Assert.assertEquals(1,roundHourOutputList.get(3)._2.getNumTw());
                    Assert.assertEquals(1,roundHourOutputList.get(3)._2.getNumRtw());
                    Assert.assertEquals(0,roundHourOutputList.get(3)._2.getNumReply());
                    
                    Assert.assertEquals(1.126F,roundHourOutputList.get(4)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.234F,roundHourOutputList.get(4)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T17:00:00+01:00").toInstant()),
                                        roundHourOutputList.get(4)._1.getDate());
                    Assert.assertEquals(1,roundHourOutputList.get(4)._2.getNumTw());
                    Assert.assertEquals(0,roundHourOutputList.get(4)._2.getNumRtw());
                    Assert.assertEquals(1,roundHourOutputList.get(4)._2.getNumReply());
                    
                    
                    //TEST ON ROUND DAY
                    List<Tuple2<GeoLocTruncTimeKey,StatsCounter>> roundDayOutputList=egr.getGeoCountOutputListRound().get(3);
                    Assert.assertEquals(4,roundDayOutputList.size());
                    sortGeoTimeList(roundDayOutputList);
                    
                    Assert.assertEquals(1.123F,roundDayOutputList.get(0)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.233F,roundDayOutputList.get(0)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T00:00:00+01:00").toInstant()),
                                        roundDayOutputList.get(0)._1.getDate());
                    Assert.assertEquals(4,roundDayOutputList.get(0)._2.getNumTw());
                    Assert.assertEquals(2,roundDayOutputList.get(0)._2.getNumRtw());
                    Assert.assertEquals(1,roundDayOutputList.get(0)._2.getNumReply());
                    
                    Assert.assertEquals(1.123F,roundDayOutputList.get(1)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.234F,roundDayOutputList.get(1)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T00:00:00+01:00").toInstant()),
                                        roundDayOutputList.get(1)._1.getDate());
                    Assert.assertEquals(3,roundDayOutputList.get(1)._2.getNumTw());
                    Assert.assertEquals(0,roundDayOutputList.get(1)._2.getNumRtw());
                    Assert.assertEquals(0,roundDayOutputList.get(1)._2.getNumReply());
                    
                    Assert.assertEquals(1.126F,roundDayOutputList.get(2)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.234F,roundDayOutputList.get(2)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T00:00:00+01:00").toInstant()),
                                        roundDayOutputList.get(2)._1.getDate());
                    Assert.assertEquals(1,roundDayOutputList.get(2)._2.getNumTw());
                    Assert.assertEquals(1,roundDayOutputList.get(2)._2.getNumRtw());
                    Assert.assertEquals(0,roundDayOutputList.get(2)._2.getNumReply());
                    
                    Assert.assertEquals(1.126F,roundDayOutputList.get(3)._1.getGeoLocTruncKey().getLatTrunc(),3);
                    Assert.assertEquals(2.234F,roundDayOutputList.get(3)._1.getGeoLocTruncKey().getLongTrunc(),3);
                    Assert.assertEquals(Date.from(ZonedDateTime.parse("2015-02-12T00:00:00+01:00").toInstant()),
                                        roundDayOutputList.get(3)._1.getDate());
                    Assert.assertEquals(1,roundDayOutputList.get(3)._2.getNumTw());
                    Assert.assertEquals(0,roundDayOutputList.get(3)._2.getNumRtw());
                    Assert.assertEquals(1,roundDayOutputList.get(3)._2.getNumReply());
                    
                })
                .executeTest(30000);
                
    }
    
    private void sortGeoTimeList(List<Tuple2<GeoLocTruncTimeKey, StatsCounter>> roundOutputList) {
        roundOutputList.sort((t1, t2) -> {
            int comp = t1._1.getDate().compareTo(t2._1.getDate());
            if (comp == 0) {
                int comp2 = ((Double) t1._1.getGeoLocTruncKey().getLatTrunc()).compareTo(t2._1.getGeoLocTruncKey().getLatTrunc());
                return comp2 != 0 ? comp2 : ((Double) t1._1.getGeoLocTruncKey().getLongTrunc()).compareTo(t2._1.getGeoLocTruncKey().getLongTrunc());
            }
            return comp;
        });
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

        public List<List<Tuple2<GeoLocTruncTimeKey, StatsCounter>>> getGeoCountOutputListRound() {
            return geoCountOutputListRound;
        }

        public void setGeoCountOutputListRound(List<List<Tuple2<GeoLocTruncTimeKey, StatsCounter>>> geoCountOutputListRound) {
            this.geoCountOutputListRound = geoCountOutputListRound;
        }
        
         public void addGeoCountOutputListRound(List<Tuple2<GeoLocTruncTimeKey, StatsCounter>> geoCountOutputListRound) {
            if(this.geoCountOutputListRound==null)
                this.geoCountOutputListRound=new LinkedList<>();
            this.geoCountOutputListRound.add(geoCountOutputListRound);
        }
        
        @Override
        public boolean isExpectedOutputFilled() {
            return geoCountOutputListRound.size()==expectedOutputSize; 
        }
    
    }
}
