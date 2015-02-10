package com.tilab.ca.sda.consumer.tw.tot.batch;

import com.tilab.ca.sda.consumer.tw.tot.batch.utils.Arguments;
import com.tilab.ca.sda.consumer.tw.tot.batch.utils.BatchUtils;
import com.tilab.ca.sda.consumer.tw.tot.batch.utils.CommandLineArgs;
import com.tilab.ca.sda.consumer.tw.tot.batch.utils.TwTotConsumerProperties;
import com.tilab.ca.sda.consumer.tw.tot.core.TwCounter;
import com.tilab.ca.sda.consumer.tw.tot.core.data.DateHtKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.GeoLocTruncKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.GeoLocTruncTimeKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.StatsCounter;
import com.tilab.ca.sda.consumer.tw.tot.dao.ConsumerTwTotDao;
import com.tilab.ca.sda.consumer.tw.tot.dao.ConsumerTwTotDaoDefaultImpl;
import com.tilab.ca.sda.ctw.utils.Utils;
import com.tilab.ca.sda.sda.model.GeoStatus;
import com.tilab.ca.sda.sda.model.HtsStatus;
import java.io.File;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;



public class TwTotConsumerBatchMain {
    
    private static final Logger log=Logger.getLogger(TwTotConsumerBatchMain.class);
    private static final String LOG4jPROPS_FILE_NAME="log4j.properties";
    private static final String HIBERNATE_CONF_FILE_NAME="twstats-tot-tw.cfg";
    
    
    private static final String SDA_CONF_SYSTEM_PROPERTY="SDA_CONF";
    private static final String TOT_TW_SYSTEM_PROPERTY="TOT_TW";
    
    
    
    public void main(String[] args){
        
        log.info("Start Tw tot Consumer Batch");
        log.info("Parsing commandline arguments...");
        try {
            Arguments arguments=CommandLineArgs.parseCommandLineArgs(args);
            TwTotConsumerProperties twProps=ConfigFactory.create(TwTotConsumerProperties.class);
            String confsPath=getConfsPath();
            ConsumerTwTotDao twDao=new ConsumerTwTotDaoDefaultImpl(confsPath+File.separator+HIBERNATE_CONF_FILE_NAME);
            String log4jPropsFilePath=confsPath+File.separator+LOG4jPROPS_FILE_NAME;
            PropertyConfigurator.configure(log4jPropsFilePath);
            
            SparkConf conf=new SparkConf().setAppName(twProps.appName());
	    JavaSparkContext sc=new JavaSparkContext(conf);
            String inputDataPath= StringUtils.isBlank(arguments.getInputDataPath())?twProps.defaultInputDataPath():arguments.getInputDataPath();
            if(StringUtils.isBlank(inputDataPath))
                throw new IllegalArgumentException("Input file cannot be blank. Please provide it on the properties file or by commandline argument");
            
            log.debug("Input data path is "+inputDataPath);
	    JavaRDD<String> tweetsRdd=sc.textFile(inputDataPath, twProps.minPartitions());
            
            JavaRDD<GeoStatus> geoStatus=tweetsRdd.map((tweetStr) -> BatchUtils.fromJstring2GeoStatus(tweetStr, twProps.roundPos()))
                    .filter((geoStatusOpt) -> geoStatusOpt.isPresent())
                    .map((geoOpt) -> geoOpt.get());
            
            if(arguments.getRoundMode()!=null){
                JavaPairRDD<GeoLocTruncTimeKey, StatsCounter> pairTotRDDGeoRound=TwCounter.countGeoStatuses(geoStatus, arguments.getRoundMode(), arguments.getGranMin(), arguments.getFrom(), arguments.getTo());
                twDao.saveGeoByTimeGran(pairTotRDDGeoRound);
            }else{
                JavaPairRDD<GeoLocTruncKey, StatsCounter>  pairTotGeoRDD=TwCounter.countGeoStatusesFromTimeBounds(geoStatus, arguments.getFrom(), arguments.getTo());
                twDao.saveGeoByTimeInterval(Utils.Time.zonedDateTime2Date(arguments.getFrom()), Utils.Time.zonedDateTime2Date(arguments.getTo()), pairTotGeoRDD);
            }
            
            JavaRDD<HtsStatus> htsStatus=tweetsRdd.map((tweetStr) -> BatchUtils.fromJstring2HtsStatus(tweetStr))
                    .filter((htsList) -> htsList.isPresent())
                    .flatMap((htsStatusOpt) -> htsStatusOpt.get());
                   
            if(arguments.getRoundMode()!=null){
               JavaPairRDD<DateHtKey, StatsCounter> pairTotRDDHtsRound=TwCounter.countHtsStatuses(htsStatus, arguments.getRoundMode(), arguments.getGranMin(), arguments.getFrom(), arguments.getTo());
               twDao.saveHtsByTimeGran(pairTotRDDHtsRound);
            }else{
                JavaPairRDD<String, StatsCounter> pairTotHtsRDD=TwCounter.countHtsStatusesFromTimeBounds(htsStatus, arguments.getFrom(), arguments.getTo());
                twDao.saveHtsByTimeInterval(Utils.Time.zonedDateTime2Date(arguments.getFrom()), Utils.Time.zonedDateTime2Date(arguments.getTo()), pairTotHtsRDD);
            }
               
        } catch (Exception ex) {
            log.error(ex);
        }
    }
    
    private static String getConfsPath(){
        String sdaPath=System.getenv(SDA_CONF_SYSTEM_PROPERTY)!=null?System.getenv(SDA_CONF_SYSTEM_PROPERTY):System.getProperty(SDA_CONF_SYSTEM_PROPERTY);
        String totTwConsumerConfFolderName=System.getenv(TOT_TW_SYSTEM_PROPERTY)!=null?System.getenv(TOT_TW_SYSTEM_PROPERTY):System.getProperty(TOT_TW_SYSTEM_PROPERTY);
        if(StringUtils.isBlank(sdaPath) || StringUtils.isBlank(totTwConsumerConfFolderName)){
            throw new IllegalStateException(String.format("Environment variable %s or %s not setted",SDA_CONF_SYSTEM_PROPERTY,TOT_TW_SYSTEM_PROPERTY));
        }
        return sdaPath+File.separator+totTwConsumerConfFolderName; 
    }
}
