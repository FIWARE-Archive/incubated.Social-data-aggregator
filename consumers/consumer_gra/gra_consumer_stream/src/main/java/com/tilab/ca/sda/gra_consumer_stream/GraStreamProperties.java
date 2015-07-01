package com.tilab.ca.sda.gra_consumer_stream;

import com.tilab.ca.sda.gra_consumer_batch.utils.GraConsumerProperties;
import com.tilab.ca.sda.gra_core.utils.GraConstants;
import org.aeonbits.owner.Config;

@Config.Sources({
	"file:${"+GraConstants.SDA_CONF_SYSTEM_PROPERTY+"}/${"+GraConstants.GRA_SYSTEM_PROPERTY+"}/"+GraConstants.GRA_PROPERTIES_FILE
})
public interface GraStreamProperties extends GraConsumerProperties{
    
    public String defaultRoundMode();
    public Integer granMin();
    
    @DefaultValue("480000")
    public String sparkCleanTTL();
    
    public String numMaxCore();

    public String checkpointDir();
    
    public Integer sparkBatchDurationMillis();

    public int twTotWindowDurationMillis();

    //sliding window interval
    public int twTotWindowSlidingIntervalMillis();
    
    public String keyRaw();
    
    public String busConnImplClass();
    
}
