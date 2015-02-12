package com.tilab.ca.sda.consumer.tw.tot.stream.bus;

import com.tilab.ca.sda.consumer.tw.tot.stream.TwTotConsumerProperties;
import java.util.HashMap;
import java.util.Map;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;



public class BusConnectionKafkaImpl implements BusConsumerConnection{
    
    private  Map<String, Integer> topicsMap;
    private TwTotConsumerProperties twProps;
    private JavaPairReceiverInputDStream<String, String> messages;
    
    public BusConnectionKafkaImpl(TwTotConsumerProperties twProps){
        this.twProps=twProps;
        topicsMap=new HashMap<>();
        topicsMap.put(twProps.topic(), twProps.numPartitions());
    }
    
    @Override
    public void init(JavaStreamingContext jssc){
        messages=KafkaUtils.createStream(jssc, twProps.zooKeeperQuorum(), twProps.groupID(), topicsMap);
    }
    
    @Override
    public JavaDStream<String> getDStreamByKey(String key){
        if(messages==null)
            throw new IllegalStateException("BusConsumerConnection must be initialized first.");
        
        return messages.filter((tuple2) -> key.equals(tuple2._1)).map((tuple2) -> tuple2._2);
    }
    
}
