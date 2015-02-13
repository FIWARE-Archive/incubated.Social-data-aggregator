package com.tilab.ca.sda.consumer.tw.tot.stream;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;


@Sources({
	"file:${SDA_CONF}/${TOT_TW}/TwTotConsumerProps.properties"
})
public interface TwTotConsumerProperties extends Config{
    
    public String defaultRoundMode();
    public Integer granMin();
    public int roundPos();
    
    @DefaultValue("480000")
    public String sparkCleanTTL();
    
    public String numMaxCore();

    public String checkpointDir();
    
    public Integer sparkBatchDurationMillis();

    public int twTotWindowDurationMillis();

    //sliding window interval
    public int twTotWindowSlidingIntervalMillis();
    
    public String keyGeo();
  
    public String keyHt();
    
    public String busConnImplClass();
}
