package com.tilab.ca.sda.consumer.tw.tot.core.test;

import com.tilab.ca.sda.consumer.tw.tot.core.TwCounter;
import com.tilab.ca.sda.consumer.tw.tot.core.data.DateHtKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.StatsCounter;
import com.tilab.ca.sda.ctw.utils.RoundType;
import com.tilab.ca.sda.sda.model.HtsStatus;
import com.tilab.ca.spark_test_lib.batch.SparkBatchTest;
import com.tilab.ca.spark_test_lib.streaming.annotations.SparkTestConfig;
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

@SparkTestConfig(appName = "HtCountTestCase", master = "local[2]")
public class HtCountTestCase extends SparkBatchTest implements Serializable{

    
    public HtCountTestCase() {
        super(HtCountTestCase.class);
    }
    
    @Test
    public void simpleTestCountBound() {
        $newBatchTest().sparkTest((jsc)->{
                    int numSlots=4;
                    int deltaMinutes=20;
                    int slot=deltaMinutes/numSlots;
                    
                    ZonedDateTime to=ZonedDateTime.now();
                    ZonedDateTime from=to.minusMinutes(deltaMinutes);
                    ZonedDateTime curr=from;
                    
                    List<HtsStatus> htStatusList=new LinkedList<>();
                    htStatusList.add(new HtsStatus(1, 1, "h0", Date.from(from.minusMinutes(10).toInstant()), false, false)); //before from
                    htStatusList.add(new HtsStatus(2, 1, "h1", Date.from(from.toInstant()), false, false));
                    curr=curr.plusMinutes(slot);
                    htStatusList.add(new HtsStatus(3, 2, "h1", Date.from(curr.toInstant()), true, false));
                    htStatusList.add(new HtsStatus(4, 3, "h1", Date.from(curr.toInstant()), true, false));
                    htStatusList.add(new HtsStatus(5, 4, "h1", Date.from(curr.toInstant()), false, true));
                    htStatusList.add(new HtsStatus(6, 5, "h1", Date.from(curr.toInstant()), false, true));
                    curr=curr.plusMinutes(slot);
                    htStatusList.add(new HtsStatus(7, 6, "h2", Date.from(curr.toInstant()), false, false));
                    curr=curr.plusMinutes(slot);
                    htStatusList.add(new HtsStatus(8, 7, "h3", Date.from(curr.toInstant()), false, false));
                    htStatusList.add(new HtsStatus(9, 8, "h3", Date.from(curr.toInstant()), false, false));
                    htStatusList.add(new HtsStatus(10, 9, "h3", Date.from(curr.toInstant()), false, false));
                    htStatusList.add(new HtsStatus(11, 9, "h3", Date.from(curr.plusSeconds(20).toInstant()), true, false));
                    htStatusList.add(new HtsStatus(12, 10, "h4", Date.from(to.plusMinutes(10).toInstant()), false, false)); //after to
                    
                    
                    JavaRDD<HtsStatus> htsStatuses=jsc.parallelize(htStatusList);
                   
                    JavaPairRDD<String, StatsCounter> htsStatusesFromTimeBounds= TwCounter.countHtsStatusesFromTimeBounds(htsStatuses,from,to);
                    return (htsStatusesFromTimeBounds.collect());
                })
                .test((res) ->{
                    List<Tuple2<String,StatsCounter>> lst=(List<Tuple2<String,StatsCounter>>)res;
                    Assert.assertNotNull(lst);
                    lst.sort((t1,t2) ->t1._1.compareTo(t2._1));
                    
                    Assert.assertEquals(3,lst.size());
                    Assert.assertEquals("h1",lst.get(0)._1);
                    Assert.assertEquals(1,lst.get(0)._2.getNumTw());
                    Assert.assertEquals(2,lst.get(0)._2.getNumRtw());
                    Assert.assertEquals(2,lst.get(0)._2.getNumReply());
                    
                    Assert.assertEquals("h2",lst.get(1)._1);
                    Assert.assertEquals(1,lst.get(1)._2.getNumTw());
                    Assert.assertEquals(0,lst.get(1)._2.getNumRtw());
                    Assert.assertEquals(0,lst.get(1)._2.getNumReply());
                    
                    
                    Assert.assertEquals("h3",lst.get(2)._1);
                    Assert.assertEquals(3,lst.get(2)._2.getNumTw());
                    Assert.assertEquals(1,lst.get(2)._2.getNumRtw());
                    Assert.assertEquals(0,lst.get(2)._2.getNumReply());   
                })
                .execute();
    }
    
    @Test
    public void simpleTestCountRound() {
        String startDateStr="2015-02-12T15:44:02+01:00";
        ZonedDateTime startZDate=ZonedDateTime.parse(startDateStr);
        
        $newBatchTest()
                .sparkTest((jsc)->{
                    
                    List<HtsStatus> htStatusList=new LinkedList<>();
                    htStatusList.add(new HtsStatus(1, 1, "h1", Date.from(startZDate.toInstant()), false, false)); 
                    htStatusList.add(new HtsStatus(2, 1, "h1", Date.from(startZDate.plusSeconds(3).toInstant()), false, false)); 
                    htStatusList.add(new HtsStatus(3, 2, "h1", Date.from(startZDate.plusSeconds(10).toInstant()), true, false)); 
                    htStatusList.add(new HtsStatus(4, 2, "h1", Date.from(startZDate.withSecond(59).toInstant()), false, true)); 
                    
                    ZonedDateTime after3Min=startZDate.plusMinutes(3);
                    htStatusList.add(new HtsStatus(5, 2, "h1", Date.from(after3Min.toInstant()), false, false));
                    htStatusList.add(new HtsStatus(6, 3, "h2", Date.from(after3Min.plusSeconds(10).toInstant()), true, false));
                    htStatusList.add(new HtsStatus(7, 4, "h2", Date.from(after3Min.plusSeconds(15).toInstant()), true, false));
                    htStatusList.add(new HtsStatus(8, 5, "h2", Date.from(after3Min.plusSeconds(8).toInstant()), true, false));
                    htStatusList.add(new HtsStatus(9, 6, "h2", Date.from(after3Min.plusSeconds(16).toInstant()), false, true));
                    htStatusList.add(new HtsStatus(10, 7, "h2", Date.from(after3Min.plusSeconds(5).toInstant()), false, true));
                    
                    ZonedDateTime after16Min=startZDate.plusMinutes(16);
                    
                    htStatusList.add(new HtsStatus(11, 7, "h1", Date.from(after16Min.toInstant()), true, false));
                    htStatusList.add(new HtsStatus(12, 8, "h3", Date.from(after16Min.plusSeconds(10).toInstant()), false, false));
                    htStatusList.add(new HtsStatus(13, 9, "h3", Date.from(after16Min.plusSeconds(15).toInstant()), false, false));
                    htStatusList.add(new HtsStatus(14, 10, "h3", Date.from(after16Min.plusSeconds(25).toInstant()), true, false));
                    htStatusList.add(new HtsStatus(15, 11, "h3", Date.from(after16Min.plusSeconds(44).toInstant()), false, true));
                    htStatusList.add(new HtsStatus(16, 12, "h3", Date.from(after16Min.plusSeconds(1).toInstant()), false, true));
                    
                    JavaRDD<HtsStatus> htsStatuses=jsc.parallelize(htStatusList);
                    
                    List<List<Tuple2<DateHtKey,StatsCounter>>> htsCountOutputList=new LinkedList<>();
                    
                    JavaPairRDD<DateHtKey, StatsCounter> htsStatusesFromTimeBounds= TwCounter.countHtsStatuses(htsStatuses,RoundType.ROUND_TYPE_MIN,null);
                    htsCountOutputList.add(htsStatusesFromTimeBounds.collect());
                    
                    htsCountOutputList.add(TwCounter.countHtsStatuses(htsStatuses,RoundType.ROUND_TYPE_MIN,5).collect()); //group by 5 min

                    htsCountOutputList.add(TwCounter.countHtsStatuses(htsStatuses,RoundType.ROUND_TYPE_HOUR,null).collect()); //group by hour

                    htsCountOutputList.add(TwCounter.countHtsStatuses(htsStatuses,RoundType.ROUND_TYPE_DAY,null).collect()); //group by day 
                })
                .test((res) ->{
                    List<List<Tuple2<DateHtKey,StatsCounter>>> ehr=(List<List<Tuple2<DateHtKey,StatsCounter>>>)res;
                    
                    Assert.assertEquals(4,ehr.size());
                    
                    //check round min
                    List<Tuple2<DateHtKey,StatsCounter>> roundMinOutputList=ehr.get(0);
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
                    List<Tuple2<DateHtKey,StatsCounter>> roundMinGranOutputList=ehr.get(1);
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
                    List<Tuple2<DateHtKey,StatsCounter>> roundHourOutputList=ehr.get(2);
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
                    List<Tuple2<DateHtKey,StatsCounter>> roundDayOutputList=ehr.get(3);
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
                    
                })
                .execute();
                
    }
    
}
