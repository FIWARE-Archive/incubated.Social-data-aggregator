package com.tilab.ca.sda.ctw;

import com.tilab.ca.sda.ctw.connector.TwitterReceiverBuilder;
import com.tilab.ca.sda.ctw.connector.TwitterStream;
import com.tilab.ca.sda.ctw.dao.TwStatsDao;
import com.tilab.ca.sda.ctw.data.GeoBox;
import com.tilab.ca.sda.ctw.utils.Utils;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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

    //private int BATCH_CICLE_NUM = 0;

    public TwitterStreamConnector(TwStreamConnectorProperties twProps,TwStatsDao twStatDao) {
        this.twProps = twProps;
        this.twStatDao=twStatDao;
        if (twProps.langFilter() != null) {
            log.info(String.format("[%s] lang filter enabled on %s", Constants.SDA_TW_CONNECTOR_LOG_TAG, twProps.langFilter()));
            langFilter = new HashSet<>(Arrays.asList(twProps.langFilter().split(",")));
        }
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
    }

    public void collectAndSaveTweets(JavaDStream<Status> tweetsDStream) {
        
        JavaDStream<Status> twWind = tweetsDStream.window(
                new Duration(twProps.twitterInserterWindowDuration()),
                new Duration(twProps.twitterInserterWindowSlidingInterval()));
        
        JavaDStream<Status> coalesced = twWind.transform((rdd, time) -> rdd.coalesce(1)); 
        coalesced.foreach((rdd, time) -> {
            twStatDao.saveRddData(rdd, twProps.dataOutputFolder(), twProps.dataRootFolder());
            return null;
        });
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
