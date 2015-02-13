package com.tilab.ca.sda.ctw.hibernate;

import java.io.File;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;


public class TwStatsSession {
    private static final Logger log = Logger.getLogger(TwStatsSession.class);
    private static final String HIBERNATE_LOG_TAG = "HIBERNATE-INIT";
    private static final String HIB_CONFIG_FILE_NAME="twstats.cfg.xml";

    private static String hibConfFilePath=HIB_CONFIG_FILE_NAME;
   
    private static SessionFactory sessionFactory = null;
    

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from Annotation
            Configuration cfg = new Configuration().configure(new File(hibConfFilePath));
            
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                    applySettings(cfg.getProperties());
            
            return cfg.buildSessionFactory(builder.build());
        } catch (Throwable ex) {
            log.error(String.format("[%s] ", HIBERNATE_LOG_TAG), ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static void setHibConfFilePath(String hibCFilePath){
        hibConfFilePath=hibCFilePath;
    }
    
    public static SessionFactory getSessionFactory() {
        if(sessionFactory==null)
            sessionFactory=buildSessionFactory();
        
        return sessionFactory;
    }
}
