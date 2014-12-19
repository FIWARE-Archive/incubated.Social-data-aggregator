package com.tilab.ca.sda.ctw;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.DefaultValue;
import org.aeonbits.owner.Config.Sources;

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
    
    int sparkBatchDurationMillis();

    int twitterInserterWindowDuration();

    //sliding window interval
    int twitterInserterWindowSlidingInterval();
    
    String daoClass();
}
