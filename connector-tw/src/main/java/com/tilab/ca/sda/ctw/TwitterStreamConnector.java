package com.tilab.ca.sda.ctw;

import com.tilab.ca.sda.ctw.bus.BusConnection;
import com.tilab.ca.sda.ctw.bus.BusConnectionPool;
import com.tilab.ca.sda.ctw.bus.BusConnectionPool.BusConnPoolConf;
import com.tilab.ca.sda.ctw.bus.ProducerFactory;
import com.tilab.ca.sda.ctw.connector.TwitterReceiverBuilder;
import com.tilab.ca.sda.ctw.connector.TwitterStream;
import com.tilab.ca.sda.ctw.dao.TwStatsDao;
import com.tilab.ca.sda.ctw.data.GeoBox;
import com.tilab.ca.sda.ctw.data.GeoStatus;
import com.tilab.ca.sda.ctw.data.HtsStatus;
import com.tilab.ca.sda.ctw.data.TwStatus;
import com.tilab.ca.sda.ctw.utils.JsonUtils;
import com.tilab.ca.sda.ctw.utils.TwUtils;
import com.tilab.ca.sda.ctw.utils.Utils;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import twitter4j.Status;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterStreamConnector implements Serializable{
        
    private static final Logger log = Logger.getLogger(TwitterStreamConnector.class);

    private final TwStreamConnectorProperties twProps;
    private final TwStatsDao twStatDao;
    private Set<String> langFilter = null;
    
    
    private ProducerFactory<String,String> producerFactory=null;
    private BusConnPoolConf busConnPoolConf=null;
    
    
    private static final String TW_TOPIC = "tw";
    private static final String GEO_LABEL = "geo";
    private static final String HT_LABEL = "ht";
    private static final String TW_STATUS_LABEL = "status";
    private static final String RAW_LABEL = "raw";
    
    //private int BATCH_CICLE_NUM = 0;
    
    public TwitterStreamConnector(TwStreamConnectorProperties twProps,TwStatsDao twStatDao){
        this.twProps = twProps;
        this.twStatDao=twStatDao;
        if (twProps.langFilter() != null) {
            log.info(String.format("[%s] lang filter enabled on %s", Constants.SDA_TW_CONNECTOR_LOG_TAG, twProps.langFilter()));
            langFilter = new HashSet<>(Arrays.asList(twProps.langFilter().split(",")));
        }
        //this.broadcastBusConnPool=broadcastBusConnPoolOpt;
    }
    
    
    public TwitterStreamConnector withProducerFactory(ProducerFactory producerFactory){
        this.producerFactory=producerFactory;
        return this;
    }
    
    public TwitterStreamConnector withProducerPoolConf(BusConnPoolConf busConnPoolConf){
        this.busConnPoolConf=busConnPoolConf;
        return this;
    }

    public void executeMainOperations(JavaStreamingContext jssc) throws Exception {
        log.info(String.format("[%s] starting acquisition twitter streaming..", Constants.SDA_TW_CONNECTOR_APP_NAME));
        JavaDStream<Status> tweetsDStream = TwitterStream.createStream(jssc, getTwitterReceiverBuilderFromConfigurations(twProps, twStatDao));
        if (langFilter != null) {
            tweetsDStream = tweetsDStream.filter((status) -> langFilter.contains(status.getUser().getLang()));
            log.info(String.format("[%s] received tweets have been filtered", Constants.SDA_TW_CONNECTOR_LOG_TAG));
        }
        log.info(String.format("[%s] starting collecting tweets...", Constants.SDA_TW_CONNECTOR_APP_NAME));
        collectAndSaveTweets(tweetsDStream);
        
        if(producerFactory!=null){
             log.info(String.format("[%s] BusConnectionPool is configured!", Constants.SDA_TW_CONNECTOR_APP_NAME));
            generateModelAndSendDataOnBus(tweetsDStream);
        }
    }

    public void collectAndSaveTweets(JavaDStream<Status> tweetsDStream) {
        
        JavaDStream<Status> twWind = tweetsDStream.window(
                new Duration(twProps.twitterInserterWindowDuration()),
                new Duration(twProps.twitterInserterWindowSlidingInterval()));
        
        //JavaDStream<Status> coalesced = twWind.transform((rdd, time) -> rdd.coalesce(1)); 
//        coalesced.foreach((rdd, time) -> {
//            twStatDao.saveRddData(rdd, twProps.dataOutputFolder(), twProps.dataRootFolder());
//            return null;
//        });
        twWind.foreach((rdd, time) -> {
            twStatDao.saveRddData(rdd, twProps.dataOutputFolder(), twProps.dataRootFolder());
            return null;
        });
    }
    
    public void generateModelAndSendDataOnBus(JavaDStream<Status> tweetsDStream){
       //send raw tweets
       tweetsDStream.foreachRDD((rdd) -> {
         sendRddContent(rdd,TW_TOPIC, RAW_LABEL);
         return null;
       });
       //MAP to internal model and send through the bus
       //GEO TWEETS
       JavaDStream<GeoStatus> geoStatusDStream=getTwGeoStatusFromStatus(tweetsDStream);
       geoStatusDStream.foreachRDD((rdd) -> {
           sendRddContent(rdd,TW_TOPIC, GEO_LABEL);
           return null;
       });
       //Tweets containing hashtags
       JavaDStream<HtsStatus> htsStatusDStream=getTwHtsStatusFromStatus(tweetsDStream);
       htsStatusDStream.foreachRDD((rdd) -> {
           sendRddContent(rdd,TW_TOPIC, HT_LABEL);
           return null;
       });
       JavaDStream<TwStatus> twStatusDStream=getTwStatusFromStatus(tweetsDStream);
       twStatusDStream.foreachRDD((rdd) -> {
           sendRddContent(rdd,TW_TOPIC, TW_STATUS_LABEL);
           return null;
       });
    }
    
    
    
    /*
     PRIVATE METHODS
    */
    
    private JavaDStream<GeoStatus> getTwGeoStatusFromStatus(JavaDStream<Status> tweets){
        return tweets.filter((status) -> TwUtils.isGeoLocStatus(status))
                .map((status) -> GeoStatus.geoStatusFromStatus(status));
    }
    
    private JavaDStream<HtsStatus> getTwHtsStatusFromStatus(JavaDStream<Status> tweets){
        return tweets.filter((status) -> TwUtils.statusContainsHashTags(status))
                .flatMap((status) -> HtsStatus.htsStatusesFromStatus(status));
    }
    
    private JavaDStream<TwStatus> getTwStatusFromStatus(JavaDStream<Status> tweets){
        return tweets.filter((status) -> TwUtils.isGeoLocStatus(status))
                .map((status) -> TwStatus.twStatusFromStatus(status));
    }
    
    
    
    private void sendRddContent(JavaRDD<?> rdd,
            String topic,
            String topicKey){
        
        long objNum=rdd.count(); 
        if(objNum>0){
            log.info(String.format("[%s] sending %d data on bus with topic %s and key %s",Constants.SDA_TW_CONNECTOR_APP_NAME,objNum,topic,topicKey));  
            final  ProducerFactory<String,String> pf=producerFactory;
            final BusConnPoolConf bcpConf=busConnPoolConf;
            rdd.foreachPartition((partitionOfRecordsIterator ) ->{
                if(partitionOfRecordsIterator.hasNext()){
                    BusConnectionPool.initOnce(pf,bcpConf);
                    BusConnection<String,String> conn=BusConnectionPool.getConnection();
                    partitionOfRecordsIterator.forEachRemaining((elem) -> conn.send(topic, JsonUtils.serialize(elem),topicKey));
                    BusConnectionPool.returnConnection(conn);
                }
            });
        }
    }
    
    private TwitterReceiverBuilder getTwitterReceiverBuilderFromConfigurations(TwStreamConnectorProperties twProps,
            TwStatsDao twStatDao) throws Exception {

        log.info(String.format("[%s] retrieving on monitoring keys...", Constants.SDA_TW_CONNECTOR_LOG_TAG));
        List<String> onMonKeyList = twStatDao.getOnMonKeys(twProps.nodeName());

        log.info(String.format("[%s] retrieving geo keys..", Constants.SDA_TW_CONNECTOR_LOG_TAG));
        List<GeoBox> onMonGeoList = twStatDao.getOnMonGeo(twProps.nodeName());

        if (Utils.isNullOrEmpty(onMonKeyList) && Utils.isNullOrEmpty(onMonGeoList)) {
            throw new IllegalStateException("Initialization failed: No monitoring data found on "
                    + "OnMonitoringGeo/OnMonitoringKeys");
        }
        
        log.info(String.format("[%s] retrieved %d geokeys..", Constants.SDA_TW_CONNECTOR_LOG_TAG,onMonGeoList.size()));
        
        TwitterReceiverBuilder twRecBuilder = new TwitterReceiverBuilder()
                .twitterConfiguration(getConfigurationBuilder(twProps).build())
                .trackList(onMonKeyList);

        if (!Utils.isNullOrEmpty(onMonGeoList)) {
            onMonGeoList.forEach((onMonGeo) -> twRecBuilder.addLocationFilter(onMonGeo.getLatitudeFrom(),
                    onMonGeo.getLongitudeFrom(),
                    onMonGeo.getLatitudeTo(),
                    onMonGeo.getLongitudeTo()));
        }
        return twRecBuilder;
    }

    private ConfigurationBuilder getConfigurationBuilder(TwStreamConnectorProperties twProps) {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(twProps.twConsumerKey());
        cb.setOAuthConsumerSecret(twProps.twConsumerSecret());
        cb.setOAuthAccessToken(twProps.twToken());
        cb.setOAuthAccessTokenSecret(twProps.twTokenSecret());
        if (StringUtils.isNotBlank(twProps.proxyHost())) {
            cb.setHttpProxyHost(twProps.proxyHost());
            cb.setHttpProxyPort(twProps.proxyPort());
        }

        return cb;
    }
    
}
