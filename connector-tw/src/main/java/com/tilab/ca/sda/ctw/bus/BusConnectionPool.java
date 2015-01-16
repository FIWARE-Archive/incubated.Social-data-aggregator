package com.tilab.ca.sda.ctw.bus;

import java.io.Serializable;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class BusConnectionPool implements Serializable {

    private static ObjectPool<BusConnection<String,String>> pool=null;
    
    private static BusConnPoolConf conf=null;
    private static ProducerFactory<String,String> producerFactory=null;
    

    
    public static void setConf(BusConnPoolConf conf) {
        BusConnectionPool.conf = conf;
    }

    public static void setProducerFactory(ProducerFactory<String,String> producerFactory) {
        BusConnectionPool.producerFactory = producerFactory;
    }
    
    
    /**
     * Initialize the parameters that are necessary to create the connection pool 
     * if they are not initialized, otherwise do nothing 
     * @param pFactory the ProducerFactory
     * @param bConf Configurations for the bus connection pool
     */
    public static void initOnce(ProducerFactory<String,String> pFactory,BusConnPoolConf bConf){
        if(conf==null){
            System.err.println("conf is null. Set new Conf..");
            setConf(bConf);
        }
        if(producerFactory==null){
            System.err.println("producerFactory is null. Set new producerFactory..");
            setProducerFactory(pFactory);
        }
    }
    
    /**
     * Get a connection to the bus from the connection pool
     * @return
     * @throws Exception 
     */
    public static synchronized BusConnection<String, String> getConnection() throws Exception {
        //lazy initialize the pool
        if(pool==null){
            pool=createPool();
        }
        
        return pool.borrowObject();
    }

    public static void returnConnection(BusConnection<String, String> busConn) throws Exception {
        pool.returnObject(busConn);
    }

    private static synchronized ObjectPool<BusConnection<String, String>> createPool(){
         System.out.println("Creating new pool...");
         
         if(conf!=null){
            GenericObjectPoolConfig config=new GenericObjectPoolConfig();
            config.setMaxTotal(conf.maxConnections);
            config.setMaxIdle(conf.maxIdleConnections);
            return new GenericObjectPool<>(new ProducerPool(producerFactory),config);
         }
         return new GenericObjectPool<>(new ProducerPool(producerFactory));
    }
    
    public static class BusConnPoolConf implements Serializable{

        private int maxConnections;
        private int maxIdleConnections;

        public BusConnPoolConf withMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }

        public BusConnPoolConf withMaxIdleConnections(int maxIdleConnections) {
            this.maxIdleConnections = maxIdleConnections;
            return this;
        }

    }

}
