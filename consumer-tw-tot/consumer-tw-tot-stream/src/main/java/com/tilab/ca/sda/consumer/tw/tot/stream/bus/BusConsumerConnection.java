package com.tilab.ca.sda.consumer.tw.tot.stream.bus;

import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;


public interface BusConsumerConnection {
    
    public void init(JavaStreamingContext jssc);
    
    public JavaDStream<String> getDStreamByKey(String key);
    
}
