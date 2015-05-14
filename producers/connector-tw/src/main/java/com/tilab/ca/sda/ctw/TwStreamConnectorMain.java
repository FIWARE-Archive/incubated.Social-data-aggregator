package com.tilab.ca.sda.ctw;

import com.tilab.ca.sda.ctw.bus.BusConnectionPool;
import com.tilab.ca.sda.ctw.bus.ProducerFactory;
import com.tilab.ca.sda.ctw.bus.kafka.KafkaProducerFactoryBuilder;
import com.tilab.ca.sda.ctw.dao.TwStatsDao;
import com.tilab.ca.sda.ctw.dao.TwStatsDaoDefaultImpl;
import com.tilab.ca.sda.ctw.handlers.ReStartHandler;
import com.tilab.ca.sda.ctw.handlers.StartHandler;
import com.tilab.ca.sda.ctw.handlers.StopHandler;
import com.tilab.ca.sda.ctw.hibernate.TwStatsSession;
import com.tilab.ca.sda.ctw.utils.JettyServerManager;
import com.tilab.ca.sda.ctw.utils.stream.SparkStreamingManager;
import com.tilab.ca.sda.ctw.utils.stream.SparkStreamingSystemSettings;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.SparkConf;

public class TwStreamConnectorMain {

    private static final Logger log = Logger.getLogger(TwStreamConnectorMain.class);
   
   
    
    public static void printUsage(){
         System.out.println("twStreamConnector - Java Spark Streaming application that retrieve tweets from twitter and save them on storage");
         System.out.println();
         System.out.println( "usage:");
         System.out.println( "<jar> <SDA_HOME>");
         System.out.println("<SDA_HOME>. Path where SocialDataAggregator is installed");
         System.out.println();
    }

    public static void main(String[] args) throws Exception{
        log.debug(String.format("[%s] STARTING %s application",Constants.SDA_TW_CONNECTOR_LOG_TAG,Constants.SDA_TW_CONNECTOR_APP_NAME));
        //try {
            if(args.length<1 || args[0].equals("--help")){
                printUsage();
                System.exit(1);
            }
                
            String sdaHomePath=args[0];
            String log4jPropsFilePath=sdaHomePath+File.separator+Constants.LOG4jPROPS_FILE_NAME;
            PropertyConfigurator.configure(log4jPropsFilePath);
            
            log.debug(String.format("[%s] loading properties..",Constants.SDA_TW_CONNECTOR_LOG_TAG));
            Properties props = new Properties();
            props.load(new FileInputStream(new File(sdaHomePath+File.separator+Constants.PROPS_FILE_NAME)));
            TwStreamConnectorProperties twProps = ConfigFactory.create(TwStreamConnectorProperties.class, props);
            
            if(StringUtils.isBlank(twProps.sparkCleanTTL()) || 
               twProps.sparkBatchDurationMillis()==null ||
               StringUtils.isBlank(twProps.checkpointDir())){
                
                String errMessage=String.format("[%s] The following properties cannot be left blank:"
                        + "sparkCleanTTL\n"
                        + "sparkBatchDurationMillis\n"
                        + "checkpointDir.\n"
                        + "Please provided the above configurations and restart the component",Constants.SDA_TW_CONNECTOR_LOG_TAG);
                System.err.println(errMessage);
                log.error(errMessage);
                System.exit(1);
            }
            
            log.debug(String.format("[%s] loading DAO..",Constants.SDA_TW_CONNECTOR_LOG_TAG));
            TwStatsDao twStatDao = getTwStatsDaoImpl(twProps.daoClass(),sdaHomePath);
            
            String ttl = twProps.sparkCleanTTL();
            
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
                    .withCheckpointPath(twProps.checkpointDir());
                    
            log.info(String.format("[%s] Starting jetty serve to restart connector from api",
                                                                            Constants.SDA_TW_CONNECTOR_LOG_TAG));
            JettyServerManager.newInstance()
                               .port(twProps.serverPort())
                               .addContextHandler("/startCollector", new StartHandler(strManager, twProps, twStatDao,props))
                               .addContextHandler("/stopCollector", new StopHandler(strManager))
                               .addContextHandler("/restartCollector", new ReStartHandler(strManager))
                               .startServerOnNewThread();
            
            log.info(String.format("[%s] Starting twitter connector..",Constants.SDA_TW_CONNECTOR_LOG_TAG));
            
            /*
                START SPARK STREAMING
            */
            strManager.startSparkStream((jssc) -> {
                TwitterStreamConnector tsc=new TwitterStreamConnector(twProps, twStatDao);
                if (StringUtils.isNotBlank(twProps.brokersList())){
                    log.info(String.format("[%s] Bus enabled. Data will be sent on it", Constants.SDA_TW_CONNECTOR_LOG_TAG));
                    tsc=tsc.withProducerFactory(createProducerFactory(twProps,props)).withProducerPoolConf(createBusConnectionPoolConfiguration(twProps));
                }
                tsc.executeMainOperations(jssc);
            });

        //} catch (Exception e) {
            //log.error(String.format("[%s] Exception on %s ",
                    //Constants.SDA_TW_CONNECTOR_LOG_TAG, Constants.SDA_TW_CONNECTOR_APP_NAME), e);
        //}
    }


    private static TwStatsDao getTwStatsDaoImpl(String daoClassString,String sdaHomePath) throws Exception{
        
        if(StringUtils.isBlank(daoClassString)){
             log.info(String.format("[%s] No custom implementation found for TwStatsDao. Using default.. ",Constants.SDA_TW_CONNECTOR_LOG_TAG));
             //loading hibernate confs..
             String hibConfFilePath=sdaHomePath+File.separator+Constants.HIB_CONFIG_FILE_NAME;
             TwStatsSession.setHibConfFilePath(hibConfFilePath);
             
             return new TwStatsDaoDefaultImpl(TwStatsSession.getSessionFactory());
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
    
    public static ProducerFactory createProducerFactory(TwStreamConnectorProperties twProps,Properties props) throws Exception {
        ProducerFactory producerFactory = null;
        if (StringUtils.isBlank(twProps.customProducerFactoryImpl())) {
            producerFactory = new KafkaProducerFactoryBuilder<String, String>()
                    .brokersList(twProps.brokersList())
                    .withSerializerClass(twProps.kafkaSerializationClass())
                    .requiredAcks(twProps.kafkaRequiredAcks())
                    .buildProducerFactory();
        } else {
            log.info(String.format("[%s] Custom implementation found for producerFactory. Using %s..", Constants.SDA_TW_CONNECTOR_LOG_TAG, twProps.customProducerFactoryImpl()));
            Class<?> producerFactoryImplClass = Class.forName(twProps.customProducerFactoryImpl());
            if (ProducerFactory.class.isAssignableFrom(producerFactoryImplClass)) {
                producerFactory=(ProducerFactory) producerFactoryImplClass.getConstructor(Properties.class).newInstance(props);
            } else {
                throw new IllegalArgumentException("cannot instantiate custom producerFactory impl class " + twProps.customProducerFactoryImpl() + "."
                        + " Custom class must implement ProducerFactory interface.");
            }
        }
        return producerFactory;
    }
    
    public static BusConnectionPool.BusConnPoolConf createBusConnectionPoolConfiguration(TwStreamConnectorProperties twProps){
       
        return new BusConnectionPool.BusConnPoolConf().withMaxConnections(twProps.maxTotalConnections())
                                                      .withMaxIdleConnections(twProps.maxIdleConnections());   
        
    }
    
}
