package com.tilab.ca.sda.ctw;

import com.tilab.ca.sda.ctw.bus.BusConnection;
import com.tilab.ca.sda.ctw.bus.ProducerFactory;
import com.tilab.ca.sda.ctw.mocks.TwCollectorTestProps;
import com.tilab.ca.sda.ctw.dao.TwStatsDao;
import com.tilab.ca.sda.sda.model.GeoStatus;
import com.tilab.ca.sda.sda.model.HtsStatus;
import com.tilab.ca.sda.sda.model.TwStatus;
import com.tilab.ca.sda.ctw.mocks.ExpectedOutputHandlerMsgBusImpl;
import com.tilab.ca.sda.ctw.mocks.ProducerFactoryTestImpl;
import com.tilab.ca.sda.ctw.mocks.ProducerFactoryTestImpl.SendContent;
import com.tilab.ca.sda.ctw.mocks.TwStatsDaoTestImpl;
import com.tilab.ca.sda.ctw.utils.JsonUtils;
import com.tilab.ca.sda.ctw.utils.TCPServer;
import static com.tilab.ca.sda.ctw.utils.TestUtils.assertMatches;
import com.tilab.ca.spark_test_lib.streaming.SparkStreamingTest;
import com.tilab.ca.spark_test_lib.streaming.annotations.SparkTestConfig;
import com.tilab.ca.spark_test_lib.streaming.interfaces.ExpectedOutputHandler;
import com.tilab.ca.spark_test_lib.streaming.utils.TestStreamUtils;
import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.spark.streaming.api.java.JavaDStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import org.junit.Test;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.Status;
import twitter4j.TwitterObjectFactory;

/**
 * Test that mapping between raw tweets and the internal model is done right and
 * that data are sent on the mock bus
 */
@SparkTestConfig(appName = "MappingAndBusTest", master = "local[2]", batchDurationMillis = 30000, useManualClock = true)
public class MappingAndBusTestCase extends SparkStreamingTest implements Serializable {

    private TwStreamConnectorProperties props;
    private TwStatsDao twsd = null;
    private final String BASE_PATH;

    public MappingAndBusTestCase() {
        super(MappingAndBusTestCase.class);
        props = new TwCollectorTestProps();
        String workingDir = System.getProperty("user.dir");
        /*
         Each file represents the content of a batch
         */
        BASE_PATH = String.format("%s#src#test#resources#data#",
                workingDir).replace("#", File.separator);
    }

    
    @Test
    public void testCreationCustomProducerFactoryImpl() {
        final String testTopic = "test";
        final String testMessage = "testMessage";
        final String testKey = "testKey";

        try {
            int serverPort = 9998;
            Properties ps = new Properties();
            ps.setProperty(ProducerFactoryTestImpl.TestProducer.SERVER_HOST_TEST_PROP, "localhost");
            ps.setProperty(ProducerFactoryTestImpl.TestProducer.SERVER_PORT_TEST_PROP, String.valueOf(serverPort));
            ProducerFactory<String, String> mockProdFact = TwStreamConnectorMain.createProducerFactory(props, ps);
            if (!(mockProdFact instanceof ProducerFactoryTestImpl)) {
                fail("Expected mockProdFactory to be an instance of ProducerFactoryTestImpl");
            }
            ExpectedOutputHandlerMsgBusImpl<String, String> eoh = new ExpectedOutputHandlerMsgBusImpl<>(2);
            TCPServer tcpServer = null;

            tcpServer = new TCPServer(serverPort, eoh);
            tcpServer.start();

            BusConnection<String, String> conn = mockProdFact.newInstance();
            conn.send(testTopic, testMessage);
            conn.send(testTopic, testMessage, testKey);

            Thread.sleep(1000);

            tcpServer.stop();

            SendContent<String, String> sc = (SendContent) eoh.outputList.get(0);
            assertEquals(testTopic, sc.topic);
            assertEquals(testMessage, sc.msg);

            sc = (SendContent) eoh.outputList.get(1);
            assertEquals(testTopic, sc.topic);
            assertEquals(testMessage, sc.msg);
            assertEquals(testKey, sc.key);

        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }
    
    
    @Test
    public void simpleTestOneBatch() {

//        Expected sent message are:
//        3 tweets raw
//        2 GeoStatus
//        3 TwStatus
//        6 HtsStatus
//        14 messages in total
        int expectedOutputSize = 14;
        ExpectedOutputHandlerMsgBusImpl<String, String> meoh = new ExpectedOutputHandlerMsgBusImpl<>(expectedOutputSize);
        System.err.println("meoh inizialmente ha id "+meoh.toString());
//        final ProducerFactory<String,String> mockProdFact=new ProducerFactoryTestImpl<>(meoh);
        int serverPort = 9997;
        Properties ps = new Properties();
        ps.setProperty(ProducerFactoryTestImpl.TestProducer.SERVER_HOST_TEST_PROP, "localhost");
        ps.setProperty(ProducerFactoryTestImpl.TestProducer.SERVER_PORT_TEST_PROP, String.valueOf(serverPort));
        final ProducerFactory<String, String> mockProdFact = new ProducerFactoryTestImpl<>(ps);
        TCPServer tcpServer=null;

        try {
            tcpServer = new TCPServer(serverPort, meoh);
            tcpServer.start();

            final int numBatches = 1;
            final int timeoutMillis = 30000; //30 seconds timeout
            final String filePath = BASE_PATH + File.separator + "sampleTws_1.txt";
            $newTest()
                    .expectedOutputHandler(meoh)
                    .sparkStreamJob((jssc,eoh) -> {

                        JavaDStream<Status> mockTwStream
                        = TestStreamUtils.createMockDStream(jssc, 1, filePath)
                        .map((rawJson) -> TwitterObjectFactory.createStatus(rawJson));

                        new TwitterStreamConnector(props, twsd)
                        .withProducerFactory(mockProdFact)
                        .generateModelAndSendDataOnBus(mockTwStream);

                    })
                    .test((ExpectedOutputHandler eoh) -> {
                        assertEquals(meoh.outputList.size(), expectedOutputSize);
                        meoh.outputList.forEach((elem) -> assertCreatedAtFormatIsAsExpected(elem.msg));
                        //Gson gson = new GsonBuilder().setDateFormat(master);

                        
                         //check geo elements are produced with expected content
                         List<GeoStatus> geoStatusList = meoh.outputList.stream()
                         .filter((sc) -> sc.key.equals("geo"))
                         .map((sc) -> JsonUtils.deserialize(sc.msg, GeoStatus.class))
                         .sorted((g1, g2) -> ((Long) g1.getPostId()).compareTo(g2.getPostId()))
                         .collect(Collectors.toList());
                         
                         assertEquals(2, geoStatusList.size());
                         assertEquals(1, geoStatusList.get(0).getPostId());
                         assertEquals(40.827, geoStatusList.get(0).getLatTrunc(), 3);
                         assertEquals(-73.886, geoStatusList.get(0).getLongTrunc(), 3);
                         assertEquals(40.827484, geoStatusList.get(0).getLatitude(), 6);
                         assertEquals(-73.886752, geoStatusList.get(0).getLongitude(), 6);
                         assertEquals(1, geoStatusList.get(0).getUserId());
                         assertDateIsCorrect("2014-09-26T17:00:28+02",geoStatusList.get(0).getSentTime());
                         
                         assertEquals(2, geoStatusList.get(1).getPostId());
                         assertEquals(40.827, geoStatusList.get(1).getLatTrunc(), 3);
                         assertEquals(-73.886, geoStatusList.get(1).getLongTrunc(), 3);
                         assertEquals(40.827485, geoStatusList.get(1).getLatitude(), 6);
                         assertEquals(-73.886752, geoStatusList.get(1).getLongitude(), 6);
                         assertEquals(2, geoStatusList.get(1).getUserId());
                         assertDateIsCorrect("2014-09-26T17:05:28+02",geoStatusList.get(1).getSentTime());
                         
                         //check hts elements are produced with expected content
                         List<HtsStatus> htsStatusList = meoh.outputList.stream()
                         .filter((sc) -> sc.key.equals("ht"))
                         .map((sc) -> JsonUtils.deserialize(sc.msg, HtsStatus.class))
                         .sorted((g1, g2) -> {
                             int res=((Long) g1.getPostId()).compareTo(g2.getPostId());
                             if(res==0){
                                 return g1.getHashTag().compareTo(g2.getHashTag());
                             }
                             return res;
                         })
                         .collect(Collectors.toList());
                         
                         assertEquals(6, htsStatusList.size());
                         assertEquals(1, htsStatusList.get(0).getPostId());
                         assertEquals("ht2", htsStatusList.get(0).getHashTag());
                         assertEquals(1, htsStatusList.get(0).getUserId());
                         assertEquals(false, htsStatusList.get(0).isReply());
                         assertEquals(false, htsStatusList.get(0).isRetweet());
                         assertDateIsCorrect("2014-09-26T17:00:28+02",htsStatusList.get(0).getSentTime());
                         
                         assertEquals(1, htsStatusList.get(1).getPostId());
                         assertEquals("test", htsStatusList.get(1).getHashTag());
                         assertEquals(1, htsStatusList.get(1).getUserId());
                         assertEquals(false, htsStatusList.get(1).isReply());
                         assertEquals(false, htsStatusList.get(1).isRetweet());
                         assertDateIsCorrect("2014-09-26T17:00:28+02",htsStatusList.get(1).getSentTime());
                         
                         assertEquals(2, htsStatusList.get(2).getPostId());
                         assertEquals("ht3", htsStatusList.get(2).getHashTag());
                         assertEquals(2, htsStatusList.get(2).getUserId());
                         assertEquals(false, htsStatusList.get(2).isReply());
                         assertEquals(false, htsStatusList.get(2).isRetweet());
                         assertDateIsCorrect("2014-09-26T17:05:28+02",htsStatusList.get(2).getSentTime());
                         
                         assertEquals(2, htsStatusList.get(3).getPostId());
                         assertEquals("test", htsStatusList.get(3).getHashTag());
                         assertEquals(2, htsStatusList.get(3).getUserId());
                         assertEquals(false, htsStatusList.get(3).isReply());
                         assertEquals(false, htsStatusList.get(3).isRetweet());
                         assertDateIsCorrect("2014-09-26T17:05:28+02",htsStatusList.get(3).getSentTime());
                         
                         assertEquals(3, htsStatusList.get(4).getPostId());
                         assertEquals("ht4", htsStatusList.get(4).getHashTag());
                         assertEquals(3, htsStatusList.get(4).getUserId());
                         assertEquals(false, htsStatusList.get(4).isReply());
                         assertEquals(false, htsStatusList.get(4).isRetweet());
                         assertDateIsCorrect("2014-09-26T17:06:28+02",htsStatusList.get(4).getSentTime());
                         
                         assertEquals(3, htsStatusList.get(5).getPostId());
                         assertEquals("test", htsStatusList.get(5).getHashTag());
                         assertEquals(3, htsStatusList.get(5).getUserId());
                         assertEquals(false, htsStatusList.get(5).isReply());
                         assertEquals(false, htsStatusList.get(5).isRetweet());
                         assertDateIsCorrect("2014-09-26T17:06:28+02",htsStatusList.get(5).getSentTime());
                         
                         //check twStatus elements are produced with expected content
                         List<TwStatus> twStatusList = meoh.outputList.stream()
                         .filter((sc) -> sc.key.equals("status"))
                         .map((sc) -> JsonUtils.deserialize(sc.msg, TwStatus.class))
                         .sorted((g1, g2) -> ((Long) g1.getPostId()).compareTo(g2.getPostId()))
                         .collect(Collectors.toList());

                         assertEquals(3, twStatusList.size());
                         assertEquals(1, twStatusList.get(0).getPostId());
                         assertEquals("en", twStatusList.get(0).getLang());
                         assertEquals(40.827, twStatusList.get(0).getLatTrunc(), 3);
                         assertEquals(-73.886, twStatusList.get(0).getLongTrunc(), 3);
                         assertEquals(40.827484, twStatusList.get(0).getLatitude(), 6);
                         assertEquals(-73.886752, twStatusList.get(0).getLongitude(), 6);
                         assertDateIsCorrect("2014-09-26T17:00:28+02",twStatusList.get(0).getSentTime());
                         assertEquals("Twitter for Mac", twStatusList.get(0).getSource());
                         assertEquals("mockTw1", twStatusList.get(0).getText());
                         assertEquals(1, twStatusList.get(0).getUserId());
                         assertEquals(false, twStatusList.get(0).isReply());
                         assertEquals(false, twStatusList.get(0).isRetweet());
                         assertEquals(2, twStatusList.get(0).getHts().size());
                         assertEquals(true, twStatusList.get(0).getHts().contains("test"));
                         assertEquals(true, twStatusList.get(0).getHts().contains("ht2"));
                         assertNull(twStatusList.get(0).getOriginalPostDisplayName());
                         assertEquals(0,twStatusList.get(0).getOriginalPostId());
                         assertNull(twStatusList.get(0).getOriginalPostProfileImageURL());
                         assertNull(twStatusList.get(0).getOriginalPostScreenName());
                         assertNull(twStatusList.get(0).getOriginalPostText());
                         
                         assertEquals(2, twStatusList.get(1).getPostId());
                         assertEquals("en", twStatusList.get(1).getLang());
                         assertEquals(40.827, twStatusList.get(1).getLatTrunc(), 3);
                         assertEquals(-73.886, twStatusList.get(1).getLongTrunc(), 3);
                         assertEquals(40.827485, twStatusList.get(1).getLatitude(), 6);
                         assertEquals(-73.886752, twStatusList.get(1).getLongitude(), 6);
                         assertDateIsCorrect("2014-09-26T17:05:28+02",twStatusList.get(1).getSentTime());
                         assertNull(twStatusList.get(1).getSource());
                         assertEquals("mockTw2", twStatusList.get(1).getText());
                         assertEquals(2, twStatusList.get(1).getUserId());
                         assertEquals(false, twStatusList.get(1).isReply());
                         assertEquals(false, twStatusList.get(1).isRetweet());
                         assertEquals(2, twStatusList.get(1).getHts().size());
                         assertEquals(true, twStatusList.get(1).getHts().contains("test"));
                         assertEquals(true, twStatusList.get(1).getHts().contains("ht3"));
                         assertNull(twStatusList.get(1).getOriginalPostDisplayName());
                         assertEquals(0,twStatusList.get(1).getOriginalPostId());
                         assertNull(twStatusList.get(1).getOriginalPostProfileImageURL());
                         assertNull(twStatusList.get(1).getOriginalPostScreenName());
                         assertNull(twStatusList.get(1).getOriginalPostText());
                         
                         assertEquals(3, twStatusList.get(2).getPostId());
                         assertEquals("it", twStatusList.get(2).getLang());
                         assertNull(twStatusList.get(2).getLatTrunc());
                         assertNull(twStatusList.get(2).getLongTrunc());
                         assertNull(twStatusList.get(2).getLatitude());
                         assertNull(twStatusList.get(2).getLongitude());
                         assertDateIsCorrect("2014-09-26T17:06:28+02",twStatusList.get(2).getSentTime());
                         assertEquals("Twitter for Test", twStatusList.get(2).getSource());
                         assertEquals("mockTw3", twStatusList.get(2).getText());
                         assertEquals(3, twStatusList.get(2).getUserId());
                         assertEquals(false, twStatusList.get(2).isReply());
                         assertEquals(false, twStatusList.get(2).isRetweet());
                         assertEquals(2, twStatusList.get(2).getHts().size());
                         assertEquals(true, twStatusList.get(2).getHts().contains("test"));
                         assertEquals(true, twStatusList.get(2).getHts().contains("ht4"));
                         assertNull(twStatusList.get(2).getOriginalPostDisplayName());
                         assertEquals(0,twStatusList.get(2).getOriginalPostId());
                         assertNull(twStatusList.get(2).getOriginalPostProfileImageURL());
                         assertNull(twStatusList.get(2).getOriginalPostScreenName());
                         assertNull(twStatusList.get(2).getOriginalPostText());
                         
                         List<String> statusJStringList = meoh.outputList.stream()
                         .filter((sc) -> sc.key.equals("raw"))
                         .map((sc) -> sc.msg)
                         .collect(Collectors.toList());
                         
                          assertEquals(3, statusJStringList.size()); 
                         
                    }).executeTest(numBatches, timeoutMillis);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("An unexpected exception occurred during test");
        }finally{
            try {
                if(tcpServer!=null && !tcpServer.isStopped())
                    tcpServer.stop();
            } catch (Exception ex) {
                System.err.println("failed to stop the tcp server");
                ex.printStackTrace();
            }
        }
    }
    
    
    @Test
    public void simpleTestTwoBatches(){

//        Expected sent message are:
//        2 tweets raw (italian)
//        1 GeoStatus
//        2 TwStatus
//        4 HtsStatus
//        9 messages in total
        int expectedOutputSize = 9;
        ExpectedOutputHandlerMsgBusImpl<String, String> meoh = new ExpectedOutputHandlerMsgBusImpl<>(expectedOutputSize);
//        final ProducerFactory<String,String> mockProdFact=new ProducerFactoryTestImpl<>(meoh);
        int serverPort = 9996;
        Properties ps = new Properties();
        ps.setProperty(ProducerFactoryTestImpl.TestProducer.SERVER_HOST_TEST_PROP, "localhost");
        ps.setProperty(ProducerFactoryTestImpl.TestProducer.SERVER_PORT_TEST_PROP, String.valueOf(serverPort));
        final ProducerFactory<String, String> mockProdFact = new ProducerFactoryTestImpl<>(ps);
        TCPServer tcpServer=null;
        twsd=new TwStatsDaoTestImpl(1);
        ((TwCollectorTestProps)props).setLangFilter("it");
        try {
            tcpServer = new TCPServer(serverPort, meoh);
            tcpServer.start();

            final int numBatches = 2;
            final int timeoutMillis = 30000; //30 seconds timeout
            $newTest()
                    .expectedOutputHandler(meoh)
                    .sparkStreamJob((jssc,eoh) -> {

                        JavaDStream<Status> mockTwStream
                        = TestStreamUtils.createMockDStream(BASE_PATH, jssc, 1, "sampleTws_1.txt", "sampleTws_2.txt")
                        .map((rawJson) -> TwitterObjectFactory.createStatus(rawJson));

                        new TwitterStreamConnector(props, twsd)
                        .withProducerFactory(mockProdFact)
                        .executeMainOperations(mockTwStream);

                    })
                    .test((ExpectedOutputHandler eoh) -> {
                        
                         assertEquals(9,meoh.outputList.size());
                         
                          List<GeoStatus> geoStatusList = meoh.outputList.stream()
                         .filter((sc) -> sc.key.equals("geo"))
                         .map((sc) -> JsonUtils.deserialize(sc.msg, GeoStatus.class))
                         .collect(Collectors.toList());
                          
                         assertEquals(1,geoStatusList.size()); 
                         assertEquals(6, geoStatusList.get(0).getPostId());
                         assertEquals(40.827, geoStatusList.get(0).getLatTrunc(), 3);
                         assertEquals(-73.886, geoStatusList.get(0).getLongTrunc(), 3);
                         assertEquals(40.827484, geoStatusList.get(0).getLatitude(), 6);
                         assertEquals(-73.886752, geoStatusList.get(0).getLongitude(), 6);
                         assertEquals(6, geoStatusList.get(0).getUserId());
                         assertDateIsCorrect("2014-09-26T17:09:28+02",geoStatusList.get(0).getSentTime()); 
                         
                         List<HtsStatus> htsStatusList = meoh.outputList.stream()
                         .filter((sc) -> sc.key.equals("ht"))
                         .map((sc) -> JsonUtils.deserialize(sc.msg, HtsStatus.class))
                         .sorted((g1, g2) -> {
                             int res=((Long) g1.getPostId()).compareTo(g2.getPostId());
                             if(res==0){
                                 return g1.getHashTag().compareTo(g2.getHashTag());
                             }
                             return res;
                         })
                         .collect(Collectors.toList());
                         
                         assertEquals(4, htsStatusList.size());
                         assertEquals(3, htsStatusList.get(0).getPostId());
                         assertEquals("ht4", htsStatusList.get(0).getHashTag());
                         assertEquals(3, htsStatusList.get(0).getUserId());
                         assertEquals(false, htsStatusList.get(0).isReply());
                         assertEquals(false, htsStatusList.get(0).isRetweet());
                         assertDateIsCorrect("2014-09-26T17:06:28+02",htsStatusList.get(0).getSentTime());
                         
                         assertEquals(3, htsStatusList.get(1).getPostId());
                         assertEquals("test", htsStatusList.get(1).getHashTag());
                         assertEquals(3, htsStatusList.get(1).getUserId());
                         assertEquals(false, htsStatusList.get(1).isReply());
                         assertEquals(false, htsStatusList.get(1).isRetweet());
                         assertDateIsCorrect("2014-09-26T17:06:28+02",htsStatusList.get(1).getSentTime());
                         
                         assertEquals(6, htsStatusList.get(2).getPostId());
                         assertEquals("ht7", htsStatusList.get(2).getHashTag());
                         assertEquals(6, htsStatusList.get(2).getUserId());
                         assertEquals(false, htsStatusList.get(2).isReply());
                         assertEquals(true, htsStatusList.get(2).isRetweet());
                         assertDateIsCorrect("2014-09-26T17:09:28+02",htsStatusList.get(2).getSentTime());
                         
                         assertEquals(6, htsStatusList.get(3).getPostId());
                         assertEquals("test", htsStatusList.get(3).getHashTag());
                         assertEquals(6, htsStatusList.get(3).getUserId());
                         assertEquals(false, htsStatusList.get(3).isReply());
                         assertEquals(true, htsStatusList.get(3).isRetweet());
                         assertDateIsCorrect("2014-09-26T17:09:28+02",htsStatusList.get(3).getSentTime());
                          
                        List<TwStatus> twStatusList = meoh.outputList.stream()
                         .filter((sc) -> sc.key.equals("status"))
                         .map((sc) -> JsonUtils.deserialize(sc.msg, TwStatus.class))
                         .sorted((g1, g2) -> ((Long) g1.getPostId()).compareTo(g2.getPostId()))
                         .collect(Collectors.toList());

                         assertEquals(2, twStatusList.size());
                         assertEquals(3, twStatusList.get(0).getPostId());
                         assertEquals("it", twStatusList.get(0).getLang());
                         assertNull(twStatusList.get(0).getLatTrunc());
                         assertNull(twStatusList.get(0).getLongTrunc());
                         assertNull(twStatusList.get(0).getLatitude());
                         assertNull(twStatusList.get(0).getLongitude());
                         assertDateIsCorrect("2014-09-26T17:06:28+02",twStatusList.get(0).getSentTime());
                         assertEquals("Twitter for Test", twStatusList.get(0).getSource());
                         assertEquals("mockTw3", twStatusList.get(0).getText());
                         assertEquals(3, twStatusList.get(0).getUserId());
                         assertEquals(false, twStatusList.get(0).isReply());
                         assertEquals(false, twStatusList.get(0).isRetweet());
                         assertEquals(2, twStatusList.get(0).getHts().size());
                         assertEquals(true, twStatusList.get(0).getHts().contains("test"));
                         assertEquals(true, twStatusList.get(0).getHts().contains("ht4"));
                         assertNull(twStatusList.get(0).getOriginalPostDisplayName());
                         assertEquals(0,twStatusList.get(0).getOriginalPostId());
                         assertNull(twStatusList.get(0).getOriginalPostProfileImageURL());
                         assertNull(twStatusList.get(0).getOriginalPostScreenName());
                         assertNull(twStatusList.get(0).getOriginalPostText());
                         
                         
                         assertEquals(6, twStatusList.get(1).getPostId());
                         assertEquals("it", twStatusList.get(1).getLang());
                         assertEquals(40.827, twStatusList.get(1).getLatTrunc(), 3);
                         assertEquals(-73.886, twStatusList.get(1).getLongTrunc(), 3);
                         assertEquals(40.827485, twStatusList.get(1).getLatitude(), 6);
                         assertEquals(-73.886752, twStatusList.get(1).getLongitude(), 6);
                         assertDateIsCorrect("2014-09-26T17:09:28+02",twStatusList.get(1).getSentTime());
                         assertNull(twStatusList.get(1).getSource());
                         assertEquals("mockTw6", twStatusList.get(1).getText());
                         assertEquals(6, twStatusList.get(1).getUserId());
                         assertEquals(false, twStatusList.get(1).isReply());
                         assertEquals(true, twStatusList.get(1).isRetweet());
                         assertEquals(2, twStatusList.get(1).getHts().size());
                         assertEquals(true, twStatusList.get(1).getHts().contains("test"));
                         assertEquals(true, twStatusList.get(1).getHts().contains("ht7"));
                         assertEquals("user125",twStatusList.get(1).getOriginalPostDisplayName());
                         assertEquals(125,twStatusList.get(1).getOriginalPostId());
                         assertNull(twStatusList.get(1).getOriginalPostProfileImageURL());
                         assertEquals("mockTw125",twStatusList.get(1).getOriginalPostText());                        
                         assertEquals("user125",twStatusList.get(1).getOriginalPostScreenName());
                         
                        
                         List<String> statusJStringList = meoh.outputList.stream()
                         .filter((sc) -> sc.key.equals("raw"))
                         .map((sc) -> sc.msg)
                         .collect(Collectors.toList());
                         
                          assertEquals(2, statusJStringList.size()); 
                    })
                    .executeTest(numBatches, timeoutMillis);;
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("An unexpected exception occurred during test");
        }finally{
            try {
                if(tcpServer!=null && !tcpServer.isStopped())
                    tcpServer.stop();
            } catch (Exception ex) {
                System.err.println("failed to stop the tcp server");
                ex.printStackTrace();
            }
        }
    }
    
    
    private void assertDateIsCorrect(String expectedDate, Date date2eval) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(JsonUtils.DATE_ISO_8601_FORMAT);
            assertEquals(sdf.parse(expectedDate), date2eval);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void assertCreatedAtFormatIsAsExpected(String jsonString) {
        try {
            String createdAt = null;
            JSONObject jo = new JSONObject(jsonString);
            if (jo.has("createdAt")) {
                createdAt = jo.getString("createdAt");
            } else {
                createdAt = jo.getString("sentTime");
            }

            assertMatches("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\+[0-9]{2}", createdAt);
        } catch (JSONException ex) {
            fail("not a valid json in input!");
        }
    }
}
