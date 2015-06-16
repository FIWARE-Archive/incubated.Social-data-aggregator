package com.tilab.ca.sda.gra_consumer_batch;

import com.tilab.ca.sda.consumer.utils.BatchUtils;
import com.tilab.ca.sda.ctw.utils.Utils;
import com.tilab.ca.sda.gra_consumer_batch.utils.Arguments;
import com.tilab.ca.sda.gra_consumer_batch.utils.CommandLineArgs;
import com.tilab.ca.sda.gra_consumer_batch.utils.GraConsumerProperties;
import com.tilab.ca.sda.gra_consumer_dao.GraConsumerDao;
import com.tilab.ca.sda.gra_consumer_dao.data.TwGenderProfile;
import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.gra_core.components.GRA;
import com.tilab.ca.sda.gra_core.utils.GraConstants;
import com.tilab.ca.sda.sda.model.TwUserProfile;
import java.io.File;
import java.util.Properties;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;
import scala.Tuple2;

public class GraConsumerBatchMain {

    private static final Logger log = Logger.getLogger(GraConsumerBatchMain.class);

    private static final String APP_NAME = "GraConsumerBatch";
    private static final String DAO_IMPL_CONF_FILE_NAME="dao_impl.conf";

    public static void main(String[] args) {

        try {
            String confsPath=Utils.Env.getConfsPathFromEnv(GraConstants.SDA_CONF_SYSTEM_PROPERTY, GraConstants.GRA_SYSTEM_PROPERTY);
            String log4jPropsFilePath=confsPath+File.separator+GraConstants.LOG4jPROPS_FILE_NAME;
            PropertyConfigurator.configure(log4jPropsFilePath);
            
            log.info("Start Gender Recognition Algorithm Consumer Batch");
            
            log.info("Parsing commandline arguments...");
            Arguments arguments=CommandLineArgs.parseCommandLineArgs(args);
            
            String inputDataPath= arguments.getInputDataPath();
            if(StringUtils.isBlank(inputDataPath))
                throw new IllegalArgumentException("Input file cannot be blank. Please provide it on the properties file or by commandline argument");
            
            
            GraConsumerProperties graProps=loadProps(confsPath);
            log.info("Loading Gra Consumer DAO..");
            GraConsumerDao graDao=loadConsumerGraDao(confsPath, graProps.daoImplClass());
            
            SparkConf conf=new SparkConf().setAppName(APP_NAME);
	    JavaSparkContext sc=new JavaSparkContext(conf);
            log.debug("Input data path is "+inputDataPath);
            log.info("Starting gra analytics..");
            startGraAnalytics(sc, inputDataPath, graProps, graDao, arguments);
            
        } catch (Exception ex) {
            log.error("Error during execution of GraConsumerBatch",ex);
        }
    }
    
    private static void startGraAnalytics(JavaSparkContext sc,String inputDataPath,GraConsumerProperties graProps,
                                                                        GraConsumerDao graDao,Arguments arguments) throws Exception{
        JavaRDD<String> tweetsRdd=sc.textFile(inputDataPath);
        
        GRA.GRAConfig graConf=new GRA.GRAConfig().coloursClassifierModelClass(graProps.coloursModelImplClass())
                                              .descrClassifierModel(graProps.descrModelImplClass())
                                              .featureExtractorImpl(graProps.featureExtractionClassImpl())
                                              .namesGenderMapClassImpl(graProps.namesGenderMapImplClass())
                                              .trainingPath(graProps.trainingFilesPath())
                                              .numColorBitsMapping(graProps.colorAlgoReductionNumBits())
                                              .numColorBitsMapping(graProps.colorAlgoNumColorsToConsider());
        GRA gra=new GRA(graConf, sc);
        JavaRDD<ProfileGender> profilesGenders=GraEvaluateAndCount.evaluateUniqueProfilesRdd(tweetsRdd,gra);
        
        //caching on disk if data don't fit in RAM
        profilesGenders.persist(StorageLevel.MEMORY_AND_DISK());
        
        log.info(String.format("Saving %d distinct profiles on storage..",profilesGenders.count()));
        graDao.saveTwGenderProfiles(profilesGenders.map(profileGender -> new TwGenderProfile(profileGender.getTwProfile().getUid(), 
                                                                                             profileGender.getTwProfile().getScreenName(), 
                                                                                             profileGender.getGender().toChar())));
        //TODO Continuare con parte di map reduce per trovare tweet maschi femmine pagine
        
        
    }
    
    private static GraConsumerProperties loadProps(String confsPath) throws Exception{
        GraConsumerProperties twProps=ConfigFactory.create(GraConsumerProperties.class);
        
        if(twProps==null){
            Properties props = Utils.Load.loadPropertiesFromPath(confsPath);
            twProps = ConfigFactory.create(GraConsumerProperties.class, props);
        }
        
        return twProps;
    }
    
    
    private static GraConsumerDao loadConsumerGraDao(String confsPath,String implClassStr) throws Exception {
        Properties props=Utils.Load.loadPropertiesFromPath(confsPath+File.separator+DAO_IMPL_CONF_FILE_NAME);
        props.put(GraConsumerDao.CONF_PATH_PROPS_KEY, confsPath);
        return Utils.Load.getClassInstFromInterface(GraConsumerDao.class, implClassStr, props);
    }
}
