package com.tilab.ca.sda.ctw.bus.kafka;

import java.io.Serializable;
import java.util.Properties;

public class KafkaProducerFactoryBuilder<K,V> implements Serializable{
    
    private static final long serialVersionUID = -5594486589791539226L;

	private Properties properties=null;
	
	private static final String METADATA_BROKER_LIST="metadata.broker.list";
	private static final String SERIALIZER_CLASS="serializer.class";
	private static final String PARTITIONER_CLASS="partitioner.class";
	private static final String REQUEST_REQUIRED_ACKS="request.required.acks";
	
	public KafkaProducerFactoryBuilder(){
		properties=new Properties();
	}
        
        public KafkaProducerFactoryBuilder<K,V> brokersList(String brokersList){
		properties.put(METADATA_BROKER_LIST,brokersList);
		return this;
	}
        
        public KafkaProducerFactoryBuilder<K,V> withSerializerClass(String serializerClass){
		properties.put(SERIALIZER_CLASS,serializerClass);
		return this;
	}
	
	public KafkaProducerFactoryBuilder<K,V> withPartitionerClass(String partitionerClass){
		properties.put(PARTITIONER_CLASS,partitionerClass);
		return this;
	}
	
	public KafkaProducerFactoryBuilder<K,V> requiredAcks(int requiredAcks){
		properties.put(REQUEST_REQUIRED_ACKS,String.valueOf(requiredAcks));
		return this;
	}
	
        
        public KafkaProducerFactory<K,V> buildProducerFactory(){
            if(atLeast1PropIsNull(METADATA_BROKER_LIST,SERIALIZER_CLASS,REQUEST_REQUIRED_ACKS))
			throw new IllegalStateException("builder props cannot be null");
		
		return new KafkaProducerFactory<>(properties);
        }
        
	
	public boolean atLeast1PropIsNull(String... propsKey){
		
		for(int i=0;i<propsKey.length;i++)
			if(properties.get(propsKey[i])==null)
				return true;
		
		return false;
	}
}
