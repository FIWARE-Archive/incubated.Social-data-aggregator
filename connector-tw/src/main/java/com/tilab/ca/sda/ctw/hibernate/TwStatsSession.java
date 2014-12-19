package com.tilab.ca.sda.ctw.hibernate;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;


public class TwStatsSession {
    private static final Logger log = Logger.getLogger(TwStatsSession.class);
    private static final String HIBERNATE_LOG_TAG = "HIBERNATE-INIT";
    private static final String HIB_CONFIG_FILE_NAME="twstats.cfg.xml";

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from Annotation
            Configuration cfg = new Configuration();
            cfg.configure(HIB_CONFIG_FILE_NAME);
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                    applySettings(cfg.getProperties());
            SessionFactory sessionFactory = cfg.buildSessionFactory(builder.build());
            return sessionFactory;
        } catch (Throwable ex) {
            log.error(String.format("[%s] ", HIBERNATE_LOG_TAG), ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
