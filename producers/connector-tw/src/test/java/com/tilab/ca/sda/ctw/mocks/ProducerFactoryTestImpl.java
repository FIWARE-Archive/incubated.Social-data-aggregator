package com.tilab.ca.sda.ctw.mocks;

import com.tilab.ca.sda.ctw.bus.BusConnection;
import com.tilab.ca.sda.ctw.bus.ProducerFactory;
import com.tilab.ca.sda.ctw.utils.JsonUtils;
import com.tilab.ca.sda.ctw.utils.TCPClient;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ProducerFactoryTestImpl<K,V> implements ProducerFactory<K, V>{

    private Properties props;
    private TCPClient tcpClient;
    //private ExpectedOutputHandlerMsgBusImpl<K,V> eoh;
    
    public ProducerFactoryTestImpl(Properties initProps){
        props=initProps;
    }
    
    
    @Override
    public BusConnection<K, V> newInstance() {
            return new TestProducer<>(props);
    }
    
    
    public static class TestProducer<K,V> implements BusConnection<K, V>{

        //private ExpectedOutputHandlerMsgBusImpl<K,V> eoh=null;
        private Properties props=null;
        private TCPClient tcpClient;
        public static final String SERVER_PORT_TEST_PROP="server.port";
        public static final String SERVER_HOST_TEST_PROP="server.host";
        
        
        public TestProducer(Properties props){
            try {
                this.props=props;
                tcpClient=new TCPClient(props.getProperty(SERVER_HOST_TEST_PROP), Integer.parseInt(props.getProperty(SERVER_PORT_TEST_PROP)));
            } catch (IOException ex) {
                throw new RuntimeException("Failed to create tcpClient");
            }
        }
        
        
        @Override
        public void send(String providedTopic, V msg) {
           SendContent sc=new SendContent<>(providedTopic,msg);
           tcpClient.sendToServer(JsonUtils.serialize(sc));
        }

        @Override
        public void send(String providedTopic, V msg, K providedKey) {
            SendContent sc=new SendContent<>(providedTopic,msg,providedKey);
            tcpClient.sendToServer(JsonUtils.serialize(sc));
           //eoh.addOutputItem(new SendContent<>(providedTopic,msg,providedKey));
        }

        @Override
        public void dispose() {
            tcpClient.closeConnection();
        }   
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
