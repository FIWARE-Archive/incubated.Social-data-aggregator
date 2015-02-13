package com.tilab.ca.sda.consumer.tw.tot.batch.utils;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;


@Sources({
	"file:${SDA_CONF}/${TOT_TW}/TwTotConsumerProps.properties"
})
public interface TwTotConsumerProperties extends Config{
    
    public String defaultInputDataPath();
    public String defaultRoundMode();
    public Integer granMin();
    public int roundPos();
    
    public String daoImplClass();
    
    @DefaultValue("1")
    public int minPartitions();
}
