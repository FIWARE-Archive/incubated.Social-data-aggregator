package com.tilab.ca.sda.ctw;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tilab.ca.sda.ctw.bus.BusConnection;
import com.tilab.ca.sda.ctw.bus.ProducerFactory;
import com.tilab.ca.sda.ctw.mocks.TwCollectorTestProps;
import com.tilab.ca.sda.ctw.dao.TwStatsDao;
import com.tilab.ca.sda.ctw.data.GeoStatus;
import com.tilab.ca.sda.ctw.mocks.ExpectedOutputHandlerMsgBusImpl;
import com.tilab.ca.sda.ctw.mocks.ProducerFactoryTestImpl;
import com.tilab.ca.sda.ctw.mocks.ProducerFactoryTestImpl.SendContent;
import com.tilab.ca.sda.ctw.utils.JsonUtils;
import com.tilab.ca.sda.ctw.utils.TCPServer;
import static com.tilab.ca.sda.ctw.utils.TestUtils.assertMatches;
import com.tilab.ca.spark_test_lib.streaming.SparkStreamingTest;
import com.tilab.ca.spark_test_lib.streaming.annotations.SparkTestConfig;
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
//        3 GeoStatus
//        3 TwStatus
//        6 HtsStatus
//        15 messages in total
        int expectedOutputSize = 14;
        ExpectedOutputHandlerMsgBusImpl<String, String> meoh = new ExpectedOutputHandlerMsgBusImpl<>(expectedOutputSize);
//        final ProducerFactory<String,String> mockProdFact=new ProducerFactoryTestImpl<>(meoh);
        int serverPort = 9999;
        Properties ps = new Properties();
        ps.setProperty(ProducerFactoryTestImpl.TestProducer.SERVER_HOST_TEST_PROP, "localhost");
        ps.setProperty(ProducerFactoryTestImpl.TestProducer.SERVER_PORT_TEST_PROP, String.valueOf(serverPort));
        final ProducerFactory<String, String> mockProdFact = new ProducerFactoryTestImpl<>(ps);
        TCPServer tcpServer;

        try {
            tcpServer = new TCPServer(serverPort, meoh);
            tcpServer.start();

            final int numBatches = 1;
            final int timeoutMillis = 30000; //10 seconds timeout
            final String filePath = BASE_PATH + File.separator + "sampleTws_1.txt";
            $newTest()
                    .expectedOutputHandler(meoh)
                    .sparkStreamJob((jssc) -> {

                        JavaDStream<Status> mockTwStream
                        = TestStreamUtils.createMockDStream(jssc, 1, filePath)
                        .map((rawJson) -> TwitterObjectFactory.createStatus(rawJson));

                        new TwitterStreamConnector(props, twsd)
                        .withProducerFactory(mockProdFact)
                        .generateModelAndSendDataOnBus(mockTwStream);

                    })
                    .test((eoh) -> {
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
                         
                         
                         
                    }).executeTest(numBatches, timeoutMillis);
            
                    tcpServer.stop();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("An unexpected exception occurred during test");
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

    //test status.toString() and JsonUtils.serialize produce the same output
    //test lang filter
}
