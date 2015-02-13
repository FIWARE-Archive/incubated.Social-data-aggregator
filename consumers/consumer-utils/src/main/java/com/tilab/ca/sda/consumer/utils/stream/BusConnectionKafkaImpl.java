package com.tilab.ca.sda.consumer.utils.stream;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;



public class BusConnectionKafkaImpl implements BusConsumerConnection{
    
    private static final String TOPIC_PROP="topic";
    private static final String NUM_PARTITIONS_PROP="numPartitions";
    private static final String ZOOKEEPER_QUORUM_PROP="zooKeeperQuorum";
    private static final String GROUP_ID_PROP="groupID";
    
    private final  Map<String, Integer> topicsMap;
    
    
    private JavaPairReceiverInputDStream<String, String> messages;
    private final String zooKeeperQuorum;
    private final String groupID;
    
    public BusConnectionKafkaImpl(Properties props){
        topicsMap=new HashMap<>();
        topicsMap.put(props.getProperty(TOPIC_PROP), Integer.parseInt(props.getProperty(NUM_PARTITIONS_PROP)));
        zooKeeperQuorum=props.getProperty(ZOOKEEPER_QUORUM_PROP);
        groupID=props.getProperty(GROUP_ID_PROP);
    }
    
    @Override
    public void init(JavaStreamingContext jssc){
        messages=KafkaUtils.createStream(jssc, zooKeeperQuorum, groupID, topicsMap);
    }
    
    @Override
    public JavaDStream<String> getDStreamByKey(String key){
        if(messages==null)
            throw new IllegalStateException("BusConsumerConnection must be initialized first.");
        
        return messages.filter((tuple2) -> key.equals(tuple2._1)).map((tuple2) -> tuple2._2);
    }
    
}
