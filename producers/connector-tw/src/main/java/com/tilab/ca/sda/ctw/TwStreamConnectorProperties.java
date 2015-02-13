package com.tilab.ca.sda.ctw;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.DefaultValue;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

@LoadPolicy(LoadType.MERGE)
@Sources({
    "classpath:TwStreamConnector.properties"
})
public interface TwStreamConnectorProperties extends Config{
    
    //proxy setting
    int proxyPort();

    String proxyHost();

    /*
     * Tokens for twitter connection
     * */
    String twConsumerKey();

    String twConsumerSecret();

    String twToken();

    String twTokenSecret();
    
    
    //if a filter on tweet language is needed (e.g. it,es)
    String langFilter();
    
   //Identifies the node that monitor a set of keys
    String nodeName();

    @DefaultValue("8088")
    int serverPort();

    int savePartitions();
    
    //root folder on which will be saved raw tweets
    String dataOutputFolder();
    String dataRootFolder();

    /**
     * SPARK properties
     */
    
    @DefaultValue("480000")
    String sparkCleanTTL();
    
    String numMaxCore();

    String checkpointDir();
    
    Integer sparkBatchDurationMillis();

    int twitterInserterWindowDuration();

    //sliding window interval
    int twitterInserterWindowSlidingInterval();
    
    String daoClass();
    
    String brokersList();
    
    String kafkaSerializationClass();
    
    int kafkaRequiredAcks();
    
    @DefaultValue("10")
    int maxTotalConnections();
    
    @DefaultValue("10")
    int maxIdleConnections();
    
    String customProducerFactoryImpl();
    
}
