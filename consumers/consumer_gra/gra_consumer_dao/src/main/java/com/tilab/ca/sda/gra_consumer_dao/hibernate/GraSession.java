package com.tilab.ca.sda.gra_consumer_dao.hibernate;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;


public class GraSession {
    private static SessionFactory sessionFactory = null;
    private static final Logger log = Logger.getLogger(GraSession.class);
    
    private static SessionFactory buildSessionFactory(Configuration cfg) {
        try {
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                    applySettings(cfg.getProperties());
            
            return cfg.buildSessionFactory(builder.build());
        } catch (Throwable ex) {
            log.error(ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
     
     
     public static SessionFactory getSessionFactory(Configuration cfg) {
         if(sessionFactory==null)
             sessionFactory=buildSessionFactory(cfg);
         
        return sessionFactory;
    }
}
