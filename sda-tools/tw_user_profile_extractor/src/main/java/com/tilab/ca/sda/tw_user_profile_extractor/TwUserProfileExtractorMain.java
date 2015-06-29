package com.tilab.ca.sda.tw_user_profile_extractor;

import com.tilab.ca.sda.tw_user_profile_extractor.dao.TwExtractorDao;
import com.tilab.ca.sda.tw_user_profile_extractor.dao.TwExtractorDaoImpl;
import com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping.TwProfile;
import com.tilab.ca.sda.tw_user_profile_extractor.utils.Arguments;
import com.tilab.ca.sda.tw_user_profile_extractor.utils.CommandLineArgs;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class TwUserProfileExtractorMain {

    private static final Logger log = Logger.getLogger(TwUserProfileExtractorMain.class);
    

    public static void main(String args[]) {
        try {
            log.info("Tw Profile Extractor START");
            //String confsPath=Utils.Env.getConfsPathFromEnv(ProfileExtractorConsts.SDA_CONF_SYSTEM_PROPERTY, 
              //                                             ProfileExtractorConsts.PE_SYSTEM_PROPERTY)+File.separator;
            
            String confsPath="";
            String log4jPropsFilePath=confsPath+ProfileExtractorConsts.LOG4jPROPS_FILE_NAME;
            PropertyConfigurator.configure(log4jPropsFilePath);
            
            log.info("Start Tw Profile Extractor Tool");
            log.info("Parsing commandline arguments...");
            
            Arguments arguments=CommandLineArgs.parseCommandLineArgs(args);
            
            SparkConf conf=new SparkConf().setAppName(ProfileExtractorConsts.APP_NAME);
	    JavaSparkContext sc=new JavaSparkContext(conf);
            JavaRDD<TwProfile> twProfiles=TwProfileExtractor.extractUsrProfiles(sc,arguments.getInputDataPath(),arguments.areTrainingData());
            log.info(String.format("found %d distinct profiles. Provide to save them on storage..",twProfiles.count()));
            TwExtractorDao twDao=new TwExtractorDaoImpl(confsPath+ProfileExtractorConsts.HIBERNATE_CONF_FILE_NAME);
            twDao.saveTwProfilesOnStorage(twProfiles, arguments.getOutputPath());
            
            log.info("Tw Profile Extractor END");
        } catch (Exception ex) {
            log.error(ex);
        }
    }

    
}
