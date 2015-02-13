/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.ctw.bus.kafka;

import com.tilab.ca.sda.ctw.bus.BusConnection;
import com.tilab.ca.sda.ctw.bus.ProducerFactory;
import java.util.Properties;


public class KafkaProducerFactory<K,V> implements ProducerFactory<K,V>{
    
        private Properties confs;
        
        public KafkaProducerFactory(Properties confProps){
            confs=confProps;
        }
        
        @Override
        public BusConnection<K,V> newInstance(){
            return new KafkaProducer<>(confs);
        }
}
