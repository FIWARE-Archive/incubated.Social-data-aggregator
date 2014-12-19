package com.tilab.ca.sda.ctw;

import com.tilab.ca.sda.ctw.dao.TwStatsDao;
import com.tilab.ca.sda.ctw.dao.TwStatsDaoDefaultImpl;
import com.tilab.ca.sda.ctw.handlers.ReStartHandler;
import com.tilab.ca.sda.ctw.handlers.StartHandler;
import com.tilab.ca.sda.ctw.handlers.StopHandler;
import com.tilab.ca.sda.ctw.utils.JettyServerManager;
import com.tilab.ca.sda.ctw.utils.stream.SparkStreamingManager;
import com.tilab.ca.sda.ctw.utils.stream.SparkStreamingSystemSettings;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;

public class TwStreamConnectorMain {

    private static final Logger log = Logger.getLogger(TwStreamConnectorMain.class);
   

    public static void main(String[] args) {
        log.debug(String.format("[%s] STARTING %s application",Constants.SDA_TW_CONNECTOR_LOG_TAG,Constants.SDA_TW_CONNECTOR_APP_NAME));
        try {
            log.debug(String.format("[%s] loading properties..",Constants.SDA_TW_CONNECTOR_LOG_TAG));
            TwStreamConnectorProperties twProps = ConfigFactory.create(TwStreamConnectorProperties.class);
            
            log.debug(String.format("[%s] loading DAO..",Constants.SDA_TW_CONNECTOR_LOG_TAG));
            TwStatsDao twStatDao = getTwStatsDaoImpl(twProps.daoClass());
            
            
            String ttl = twProps.sparkCleanTTL();
            log.trace(String.format("[%s] sparkCleanTTL value is %s",Constants.SDA_TW_CONNECTOR_LOG_TAG,ttl));
            
            //setup spark configuration
            SparkConf sparkConf = new SparkConf().setAppName(Constants.SDA_TW_CONNECTOR_APP_NAME)
                    .set(SparkStreamingSystemSettings.SPARK_CLEANER_TTL_PROPERTY, ttl); // Enable meta-data cleaning in Spark (so this can run forever)
            
            //if there are other streaming applications running on the same cluster set this property to avoid them wait forever
            if(StringUtils.isNotBlank(twProps.numMaxCore())){
                log.debug(String.format("[%s] setting numMaxCore for this streaming application to %s..",Constants.SDA_TW_CONNECTOR_LOG_TAG,twProps.numMaxCore()));
                sparkConf = sparkConf.set(SparkStreamingSystemSettings.SPARK_CORES_MAX_PROPERTY, twProps.numMaxCore());
            }
            
            log.debug(String.format("[%s] Setting up streaming manager..",Constants.SDA_TW_CONNECTOR_LOG_TAG));
            SparkStreamingManager strManager = SparkStreamingManager.$newStreamingManager()
                    .withBatchDurationMillis(twProps.sparkBatchDurationMillis())
                    .withSparkConf(sparkConf)
                    .withCheckpointPath(twProps.checkpointDir())
                    .setUpSparkStreaming();

            log.info(String.format("[%s] Starting jetty serve for restart connector from api",
                                                                            Constants.SDA_TW_CONNECTOR_LOG_TAG));
            JettyServerManager.newInstance()
                               .port(twProps.serverPort())
                               .addContextHandler("/startCollector", new StartHandler(strManager, twProps, twStatDao))
                               .addContextHandler("/stopCollector", new StopHandler(strManager))
                               .addContextHandler("/restartCollector", new ReStartHandler(strManager))
                               .startServerOnNewThread();
            
            log.info(String.format("[%s] Starting twitter connector..",Constants.SDA_TW_CONNECTOR_LOG_TAG));
            strManager.startSparkStream((jssc) -> {
                TwitterStreamConnector tsc=new TwitterStreamConnector(twProps, twStatDao);
                tsc.executeMainOperations(jssc);
            });

        } catch (Exception e) {
            log.error(String.format("[%s] Exception on %s ",
                    Constants.SDA_TW_CONNECTOR_LOG_TAG, Constants.SDA_TW_CONNECTOR_APP_NAME), e);
        }
    }


    private static TwStatsDao getTwStatsDaoImpl(String daoClassString) throws Exception{
        
        if(StringUtils.isBlank(daoClassString)){
             log.info(String.format("[%s] No custom implementation found for TwStatsDao. Using default.. ",Constants.SDA_TW_CONNECTOR_LOG_TAG));
             return new TwStatsDaoDefaultImpl();
        }else{
            log.info(String.format("[%s] Custom implementation found for TwStatsDao. Using %s",Constants.SDA_TW_CONNECTOR_LOG_TAG,daoClassString));
            Class<?> daoImplClass=Class.forName(daoClassString);
            if(TwStatsDao.class.isAssignableFrom(daoImplClass)){
                return (TwStatsDao) daoImplClass.newInstance();
            }else{
                throw new IllegalArgumentException("cannot instantiate dao from class "+daoClassString+"."
                        + " Custom class must implement TwStatsDao interface.");
            }
        }
    }
}
