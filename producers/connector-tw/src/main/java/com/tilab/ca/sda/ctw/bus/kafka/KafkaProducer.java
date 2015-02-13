package com.tilab.ca.sda.ctw.bus.kafka;

import com.tilab.ca.sda.ctw.Constants;
import com.tilab.ca.sda.ctw.bus.BusConnection;
import java.util.Properties;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.jboss.logging.Logger;

public class KafkaProducer<K, V> implements BusConnection<K,V>{

    private static final long serialVersionUID = 5603104375576416439L;
    private static final Logger log=Logger.getLogger(KafkaProducer.class);
    private Producer<K, V> producer = null;

    public KafkaProducer(Properties props) {
        ProducerConfig pconf = new ProducerConfig(props);
        producer = new Producer<>(pconf);
    }

    @Override
    public void send(String providedTopic,V msg) {
        send(providedTopic,msg,null);
    }
    
    @Override
    public void send(String topic,V msg,K key) {
        KeyedMessage<K, V> km;
        if (key == null) {
            log.debug(String.format("[%s] Sending message %s on topic %s",Constants.SDA_TW_CONNECTOR_LOG_TAG,msg.toString(),topic));
            km = new KeyedMessage<>(topic, msg);
        } else {
            log.debug(String.format("[%s] Sending message %s on topic %s with key %s",Constants.SDA_TW_CONNECTOR_LOG_TAG,msg.toString(),topic,key.toString()));
            km = new KeyedMessage<>(topic, key, msg);
        }
        
        producer.send(km);
    }
    
    @Override
    public void dispose(){
        producer.close();
    }

}
