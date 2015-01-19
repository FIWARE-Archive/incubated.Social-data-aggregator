package com.tilab.ca.sda.ctw.mocks;

import com.tilab.ca.sda.ctw.bus.BusConnection;
import com.tilab.ca.sda.ctw.bus.ProducerFactory;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;


public class ProducerFactoryTestImpl<K,V> implements ProducerFactory<K, V>{

    private Properties props;
    
    public ProducerFactoryTestImpl(Properties initProps){
        props=initProps;
    }
    
    @Override
    public BusConnection<K, V> newInstance() {
        return new TestProducer<>();
    }
    
    
    public static class TestProducer<K,V> implements BusConnection<K, V>{

        public List<SendContent> outputList;
        
        public TestProducer(){
            outputList=new LinkedList<>();
        }
        
        @Override
        public void send(String providedTopic, V msg) {
           outputList.add(new SendContent<>(providedTopic,msg));
        }

        @Override
        public void send(String providedTopic, V msg, K providedKey) {
           outputList.add(new SendContent<>(providedTopic,msg,providedKey));
        }

        @Override
        public void dispose() {}
        
    }
    
    public static class SendContent<K,V>{
        public String topic;
        public V msg;
        public K key;

        public SendContent(String topic, V msg, K key) {
            this.topic = topic;
            this.msg = msg;
            this.key = key;
        }
        
        public SendContent(String topic, V msg) {
            this.topic = topic;
            this.msg = msg;
        }
    }
    
}
