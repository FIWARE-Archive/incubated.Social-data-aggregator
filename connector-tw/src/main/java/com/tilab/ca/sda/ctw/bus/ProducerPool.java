package com.tilab.ca.sda.ctw.bus;

import com.tilab.ca.sda.ctw.bus.BusConnection;
import com.tilab.ca.sda.ctw.bus.ProducerFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;


/**
 *
 * 
 */
public class ProducerPool<K,V> extends BasePooledObjectFactory<BusConnection<K,V>>{

    private final ProducerFactory<K,V> producerFactory;
    
    public ProducerPool(ProducerFactory<K,V> producerFactory){
        this.producerFactory=producerFactory;
    }
    
    @Override
    public BusConnection create() throws Exception {
        return producerFactory.newInstance();
    }

    @Override
    public PooledObject<BusConnection<K,V>> wrap(BusConnection t) {
        return new DefaultPooledObject<>(t);
    }

    @Override
    public void destroyObject(PooledObject<BusConnection<K,V>> p) throws Exception {
        p.getObject().dispose();
        super.destroyObject(p); 
    }
}
