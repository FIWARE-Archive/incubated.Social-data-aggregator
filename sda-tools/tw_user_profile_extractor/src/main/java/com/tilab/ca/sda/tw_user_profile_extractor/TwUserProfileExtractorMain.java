package com.tilab.ca.sda.tw_user_profile_extractor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tilab.ca.sda.ctw.utils.Utils;
import com.tilab.ca.sda.tw_user_profile_extractor.dao.TwExtractorDao;
import com.tilab.ca.sda.tw_user_profile_extractor.dao.TwExtractorDaoImpl;
import com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping.TwProfile;
import com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping.TwProfileEvaluated;
import com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping.TwRawProfile;
import com.tilab.ca.sda.tw_user_profile_extractor.utils.Arguments;
import com.tilab.ca.sda.tw_user_profile_extractor.utils.CommandLineArgs;
import java.io.File;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class TwUserProfileExtractorMain {

    private static final Logger log = Logger.getLogger(TwUserProfileExtractorMain.class);
    

    public static void main(String args[]) {
        try {
            log.info("Tw Profile Extractor START");
            String confsPath=Utils.Env.getConfsPathFromEnv(ProfileExtractorConsts.SDA_CONF_SYSTEM_PROPERTY, ProfileExtractorConsts.PE_SYSTEM_PROPERTY);
            String log4jPropsFilePath=confsPath+File.separator+ProfileExtractorConsts.LOG4jPROPS_FILE_NAME;
            PropertyConfigurator.configure(log4jPropsFilePath);
            
            log.info("Start Tw Profile Extractor Tool");
            log.info("Parsing commandline arguments...");
            
            Arguments arguments=CommandLineArgs.parseCommandLineArgs(args);
            
            SparkConf conf=new SparkConf().setAppName(ProfileExtractorConsts.APP_NAME);
	    JavaSparkContext sc=new JavaSparkContext(conf);
            JavaRDD<TwProfile> twProfiles=TwProfileExtractor.extractUsrProfiles(sc,arguments.getInputDataPath(),arguments.areTrainingData());
            TwExtractorDao twDao=new TwExtractorDaoImpl(ProfileExtractorConsts.HIBERNATE_CONF_FILE_NAME);
            twDao.saveTwProfilesOnStorage(twProfiles, arguments.getOutputPath());
            
            log.info("Tw Profile Extractor END");
        } catch (Exception ex) {
            log.error(ex);
        }
    }

    
}
