package com.tilab.ca.sda.tw_user_profile_extractor.dao;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;


public class TwProfileExtractorSession {
    
    private static final Logger log = Logger.getLogger(TwProfileExtractorSession.class);
     private static final String HIBERNATE_LOG_TAG = "HIBERNATE-INIT";
     
     private static SessionFactory sessionFactory = null;
     
     
     private static SessionFactory buildSessionFactory(Configuration cfg) {
        try {
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                    applySettings(cfg.getProperties());
            
            return cfg.buildSessionFactory(builder.build());
        } catch (Throwable ex) {
            log.error(String.format("[%s] ", HIBERNATE_LOG_TAG), ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
     
     
     public static SessionFactory getSessionFactory(Configuration cfg) {
         if(sessionFactory==null)
             sessionFactory=buildSessionFactory(cfg);
         
        return sessionFactory;
    }
}
