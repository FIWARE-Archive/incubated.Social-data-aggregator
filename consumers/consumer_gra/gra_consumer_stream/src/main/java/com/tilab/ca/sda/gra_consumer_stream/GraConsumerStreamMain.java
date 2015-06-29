package com.tilab.ca.sda.gra_consumer_stream;

import com.tilab.ca.sda.consumer.utils.stream.BusConsumerConnection;
import com.tilab.ca.sda.ctw.utils.Utils;
import com.tilab.ca.sda.ctw.utils.stream.SparkStreamingManager;
import com.tilab.ca.sda.ctw.utils.stream.SparkStreamingSystemSettings;
import com.tilab.ca.sda.gra_consumer_batch.utils.LoadUtils;
import com.tilab.ca.sda.gra_consumer_dao.GraConsumerDao;
import com.tilab.ca.sda.gra_core.utils.GraConstants;
import java.io.File;
import java.util.Properties;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.SparkConf;

public class GraConsumerStreamMain {

    private static final Logger log = Logger.getLogger(GraConsumerStreamMain.class);

    private static final String APP_NAME = "GraConsumerStream";
    public static final String BUS_CONF_FILE_NAME="bus_impl.conf";

    public static void main(String[] args) throws Exception {

        log.info("Start Gender Recognition Algorithm Consumer Stream");
        try {
            String confsPath = Utils.Env.getConfsPathFromEnv(GraConstants.SDA_CONF_SYSTEM_PROPERTY, GraConstants.GRA_SYSTEM_PROPERTY);
            System.out.println("Configuration path is "+confsPath);
            log.info("Configuration path is "+confsPath);
            String log4jPropsFilePath = confsPath + File.separator + GraConstants.LOG4jPROPS_FILE_NAME;
            PropertyConfigurator.configure(log4jPropsFilePath);
            GraStreamProperties graProps = loadProps(confsPath);

            if (StringUtils.isBlank(graProps.sparkCleanTTL())
                    || graProps.sparkBatchDurationMillis() == null
                    || StringUtils.isBlank(graProps.checkpointDir())) {

                String errMessage = "The following properties cannot be left blank:\n"
                        + "sparkCleanTTL\n"
                        + "sparkBatchDurationMillis\n"
                        + "checkpointDir.\n"
                        + "Please provided the above configurations and restart the component";
                System.err.println(errMessage);
                log.error(errMessage);
                System.exit(1);
            }
            String ttl = graProps.sparkCleanTTL();

            //setup spark configuration
            SparkConf sparkConf = new SparkConf().setAppName(APP_NAME)
                    .set(SparkStreamingSystemSettings.SPARK_CLEANER_TTL_PROPERTY, ttl); // Enable meta-data cleaning in Spark (so this can run forever)

            //if there are other streaming applications running on the same cluster set this property to avoid them wait forever
            if (StringUtils.isNotBlank(graProps.numMaxCore())) {
                log.debug(String.format("setting numMaxCore for GRA streaming application to %s..", graProps.numMaxCore()));
                sparkConf = sparkConf.set(SparkStreamingSystemSettings.SPARK_CORES_MAX_PROPERTY, graProps.numMaxCore());
            }
            
            log.info("loading dao..");
            GraConsumerDao graConsumerDao=LoadUtils.loadConsumerGraDao(confsPath, graProps.daoImplClass());
            BusConsumerConnection busConsConn=Utils.Load.getClassInstFromInterfaceAndPropsPath(BusConsumerConnection.class, 
                                                                                               graProps.busConnImplClass(), 
                                                                                               confsPath+File.separator+BUS_CONF_FILE_NAME);
            log.info("Setting up streaming manager...");
            SparkStreamingManager.$newStreamingManager()
                    .withBatchDurationMillis(graProps.sparkBatchDurationMillis())
                    .withSparkConf(sparkConf)
                    .withCheckpointPath(graProps.checkpointDir())
                    .startSparkStream(jssc -> GraStreamConsumer.start(jssc,graProps,graConsumerDao,busConsConn,confsPath));
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    private static GraStreamProperties loadProps(String confsPath) throws Exception {
        GraStreamProperties twProps = ConfigFactory.create(GraStreamProperties.class);

        if (twProps == null) {
            Properties props = Utils.Load.loadPropertiesFromPath(confsPath+File.separator+GraConstants.GRA_PROPERTIES_FILE);
            twProps = ConfigFactory.create(GraStreamProperties.class, props);
        }

        return twProps;
    }
}
