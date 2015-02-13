package com.tilab.ca.sda.consumer.tw.tot.stream;

import com.tilab.ca.sda.consumer.tw.tot.core.TotTwConstants;
import com.tilab.ca.sda.consumer.tw.tot.dao.ConsumerTwTotDao;
import com.tilab.ca.sda.consumer.tw.tot.dao.ConsumerTwTotDaoDefaultImpl;
import com.tilab.ca.sda.consumer.utils.stream.BusConsumerConnection;
import com.tilab.ca.sda.ctw.utils.Utils;
import com.tilab.ca.sda.ctw.utils.stream.SparkStreamingManager;
import com.tilab.ca.sda.ctw.utils.stream.SparkStreamingSystemSettings;
import java.io.File;
import java.util.Properties;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.SparkConf;

public class TwTotConsumerStreamMain {

    private static final Logger log = Logger.getLogger(TwTotConsumerStreamMain.class);

    private static final String APP_NAME = "twTotConsumerStream";
    
   
    public void main(String[] args) {

        String confsPath = Utils.Env.getConfsPathFromEnv(TotTwConstants.SDA_CONF_SYSTEM_PROPERTY, TotTwConstants.TOT_TW_SYSTEM_PROPERTY);
        String log4jPropsFilePath = confsPath + File.separator + TotTwConstants.LOG4jPROPS_FILE_NAME;
        PropertyConfigurator.configure(log4jPropsFilePath);
        TwTotConsumerProperties twProps = ConfigFactory.create(TwTotConsumerProperties.class);

        if (StringUtils.isBlank(twProps.sparkCleanTTL())
                || twProps.sparkBatchDurationMillis() == null
                || StringUtils.isBlank(twProps.checkpointDir())) {

            String errMessage = String.format("[%s] The following properties cannot be left blank:"
                    + "sparkCleanTTL\n"
                    + "sparkBatchDurationMillis\n"
                    + "checkpointDir.\n"
                    + "Please provided the above configurations and restart the component", TotTwConstants.TOT_TW_CONSUMER_LOG_TAG);
            System.err.println(errMessage);
            log.error(errMessage);
            System.exit(1);
        }
        try {
            log.debug(String.format("[%s] loading DAO..", TotTwConstants.TOT_TW_CONSUMER_LOG_TAG));
            ConsumerTwTotDao twDao = loadConsumerTwTotDao(confsPath,twProps.daoImplClass());
            String ttl = twProps.sparkCleanTTL();

            //setup spark configuration
            SparkConf sparkConf = new SparkConf().setAppName(APP_NAME)
                    .set(SparkStreamingSystemSettings.SPARK_CLEANER_TTL_PROPERTY, ttl); // Enable meta-data cleaning in Spark (so this can run forever)

            //if there are other streaming applications running on the same cluster set this property to avoid them wait forever
            if (StringUtils.isNotBlank(twProps.numMaxCore())) {
                log.debug(String.format("[%s] setting numMaxCore for this streaming application to %s..", TotTwConstants.TOT_TW_CONSUMER_LOG_TAG, twProps.numMaxCore()));
                sparkConf = sparkConf.set(SparkStreamingSystemSettings.SPARK_CORES_MAX_PROPERTY, twProps.numMaxCore());
            }
            
            BusConsumerConnection busConsConn=loadBusConsumerConnectionImpl(twProps.busConnImplClass());

            log.debug(String.format("[%s] Setting up streaming manager..", TotTwConstants.TOT_TW_CONSUMER_LOG_TAG));
            SparkStreamingManager strManager = SparkStreamingManager.$newStreamingManager()
                    .withBatchDurationMillis(twProps.sparkBatchDurationMillis())
                    .withSparkConf(sparkConf)
                    .withCheckpointPath(twProps.checkpointDir())
                    .setUpSparkStreaming();
            strManager.startSparkStream((jssc) -> {
                TotTwStreamConsumer.executeAnalysis(jssc, twDao, twProps,busConsConn);
            });
        } catch (Exception e) {
            log.error(e);
        }
    }

    private ConsumerTwTotDao loadConsumerTwTotDao(String confsPath,String implClassStr) throws Exception {
        Properties props=Utils.Load.loadPropertiesFromPath(confsPath);
        props.put(ConsumerTwTotDao.CONF_PATH_PROPS_KEY, confsPath);
        return Utils.Load.getClassInstFromInterface(ConsumerTwTotDao.class, implClassStr, props);
    }
    
    private BusConsumerConnection loadBusConsumerConnectionImpl(String implClassStr) throws Exception{
        return Utils.Load.getClassInstFromInterfaceAndPropsPath(BusConsumerConnection.class, implClassStr, TotTwConstants.BUS_CONF_FILE_NAME);
    }
}
