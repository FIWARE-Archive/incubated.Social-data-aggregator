package com.tilab.ca.sda.gra_consumer_batch;

import com.tilab.ca.sda.consumer.utils.BatchUtils;
import com.tilab.ca.sda.ctw.utils.Utils;
import com.tilab.ca.sda.gra_consumer_batch.utils.Arguments;
import com.tilab.ca.sda.gra_consumer_batch.utils.CommandLineArgs;
import com.tilab.ca.sda.gra_consumer_batch.utils.GraConsumerProperties;
import com.tilab.ca.sda.gra_consumer_batch.utils.LoadUtils;
import com.tilab.ca.sda.gra_consumer_dao.GraConsumerDao;
import com.tilab.ca.sda.gra_core.GenderTypes;
import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.gra_core.StatsGenderCount;
import com.tilab.ca.sda.gra_core.components.GRA;
import com.tilab.ca.sda.gra_core.components.GRAConfig;
import com.tilab.ca.sda.gra_core.utils.GraConstants;
import com.tilab.ca.sda.sda.model.GeoStatus;
import com.tilab.ca.sda.sda.model.HtsStatus;
import com.tilab.ca.sda.sda.model.keys.DateHtKey;
import com.tilab.ca.sda.sda.model.keys.GeoLocTruncKey;
import com.tilab.ca.sda.sda.model.keys.GeoLocTruncTimeKey;
import java.io.File;
import java.time.ZonedDateTime;
import java.util.Properties;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;

public class GraConsumerBatchMain {

    private static final Logger log = Logger.getLogger(GraConsumerBatchMain.class);

    private static final String APP_NAME = "GraConsumerBatch";

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
            GraConsumerDao graDao=LoadUtils.loadConsumerGraDao(confsPath, graProps.daoImplClass());
            
            SparkConf conf=new SparkConf().setAppName(APP_NAME);
            
	    JavaSparkContext sc=new JavaSparkContext(conf);
            
            log.debug("Input data path is "+inputDataPath);
            log.info("Starting gra analytics..");
            startGraAnalytics(sc,confsPath, inputDataPath, graProps, graDao, arguments);
            sc.stop();
            log.info("gra analytics END..");

        } catch (Exception ex) {
            log.error("Error during execution of GraConsumerBatch",ex);
        }
    }
    
    private static void startGraAnalytics(JavaSparkContext sc,String confsPath,String inputDataPath,GraConsumerProperties graProps,
                                                                        GraConsumerDao graDao,Arguments arguments) throws Exception{
        JavaRDD<String> tweetsRdd=sc.textFile(inputDataPath);
        
        log.info("Setting gra configuration..");
        GRAConfig graConf=new GRAConfig().coloursClassifierModel(LoadUtils.loadColourClassifierModel(confsPath, graProps.coloursModelImplClass()))
                                              .descrClassifierModel(LoadUtils.loadDescrClassifierModel(confsPath, graProps.descrModelImplClass()))
                                              .featureExtractor(LoadUtils.loadDescrFeatureExtraction(confsPath, graProps.featureExtractionClassImpl()))
                                              .namesGenderMap(LoadUtils.loadNamesGenderMap(confsPath, graProps.namesGenderMapImplClass()))
                                              .trainingPath(graProps.trainingFilesPath())
                                              .numColorBitsMapping(graProps.colorAlgoReductionNumBits())
                                              .numColorsMapping(graProps.colorAlgoNumColorsToConsider());
       
        log.info("Creating gra instance..");
        GRA gra=Utils.Load.getClassInstFromInterface(GRA.class, graProps.graClassImpl());
        gra.init(graConf, sc);
        
        log.info(String.format("filtering data in the interval from %s -> to %s",arguments.getFrom().toString(),
                                                                                 arguments.getTo().toString()));
        final ZonedDateTime from=arguments.getFrom();
        final ZonedDateTime to=arguments.getTo();
        //filter data not in the from/to interval
        tweetsRdd=tweetsRdd.filter(rawTw -> BatchUtils.isCreatedAtInRange(rawTw, from, to));
        
        //caching on disk if data don't fit in RAM
        tweetsRdd.persist(StorageLevel.MEMORY_AND_DISK());
        
        
        log.info("Evaluating unique profiles..");
        JavaRDD<ProfileGender> profilesGenders=GraEvaluateAndCount.evaluateUniqueProfilesRdd(tweetsRdd,gra);
        
        log.info(String.format("Saving %d distinct profiles on storage..",profilesGenders.count()));
        graDao.saveTwGenderProfiles(profilesGenders.map(GraResultsMapping::fromProfileGender2TwGenderProfile));
        
        log.info("Mapping unique profiles to pairRDD and caching them..");
        JavaPairRDD<Long,GenderTypes> userIdGenderPairRdd = GraEvaluateAndCount.fromProfileGenderToUserIdGenderPairRdd(profilesGenders);
        
        userIdGenderPairRdd.persist(StorageLevel.MEMORY_AND_DISK());
        
        log.info("evaluating geo analytics");
        getGeoAnalytics(tweetsRdd, userIdGenderPairRdd, graProps, arguments, graDao);
        
        log.info("evaluating hts analytics");
        getHtsAnalytics(tweetsRdd, userIdGenderPairRdd, arguments, graDao);
    }
    
    private static void getGeoAnalytics(JavaRDD<String> tweetsRdd,JavaPairRDD<Long,GenderTypes> userIdGenderPairRdd,
                                        GraConsumerProperties graProps,Arguments arguments,
                                        GraConsumerDao graDao){
        
        final int roundPos=graProps.roundPos();
        final ZonedDateTime from = arguments.getFrom();
        final ZonedDateTime to = arguments.getTo();
        Integer roundMode = arguments.getRoundMode();
        Integer granMin = arguments.getGranMin();
        
        JavaRDD<GeoStatus> geoStatus=tweetsRdd
                .filter(BatchUtils::isGeoLocStatus)
                .map((tweetStr) -> BatchUtils.fromJstring2GeoStatus(tweetStr, roundPos))
                .filter((geo) -> geo.getPostId()>0); //filter void statuses
                
        if(arguments.getRoundMode()!=null){
            JavaPairRDD<GeoLocTruncTimeKey, StatsGenderCount> pairTotRDDGeoRound=GraEvaluateAndCount.countGeoStatuses(geoStatus,userIdGenderPairRdd,roundMode, granMin);
            graDao.saveGeoByTimeGran(pairTotRDDGeoRound.map(pairGeoStats -> GraResultsMapping.fromStatsGenderCountToStatsPreGenderGeo(pairGeoStats._1, 
                                                                                              pairGeoStats._2, roundMode, granMin)));
            
        }else{
            JavaPairRDD<GeoLocTruncKey, StatsGenderCount>  pairTotGeoRDD=GraEvaluateAndCount.countGeoStatusesFromTimeBounds(geoStatus,userIdGenderPairRdd);
            graDao.saveGeoByTimeInterval(pairTotGeoRDD.map(pairGeoStatsBound -> GraResultsMapping.fromStatsGenderCountToStatsPreGenderGeoBound(pairGeoStatsBound._1, pairGeoStatsBound._2,
                                                                                                    from,to)));
        }
    }
    
    private static void getHtsAnalytics(JavaRDD<String> tweetsRdd,JavaPairRDD<Long,GenderTypes> userIdGenderPairRdd,
                                        Arguments arguments,
                                        GraConsumerDao graDao){
        
        JavaRDD<HtsStatus> htsStatus=tweetsRdd
                .filter(BatchUtils::isHtsStatus)
                .flatMap(BatchUtils::fromJstring2HtsStatus);
       
        final ZonedDateTime from = arguments.getFrom();
        final ZonedDateTime to = arguments.getTo();
        Integer roundMode = arguments.getRoundMode();
        Integer granMin = arguments.getGranMin();
                
        if(arguments.getRoundMode()!=null){
            JavaPairRDD<DateHtKey, StatsGenderCount> pairTotRDDHtsRound =GraEvaluateAndCount.countHtsStatuses(htsStatus,userIdGenderPairRdd,roundMode, granMin);
            graDao.saveHtsByTimeGran(pairTotRDDHtsRound.map(htStats -> GraResultsMapping.fromStatsGenderCountToStatsPreGenderHt(htStats._1, htStats._2, 
                                                                                                        roundMode, granMin)));
        }else{
            JavaPairRDD<String, StatsGenderCount>  pairTotHtsRDD=GraEvaluateAndCount.countHtsStatusesFromTimeBounds(htsStatus,userIdGenderPairRdd);
            graDao.saveHtsByTimeInterval(pairTotHtsRDD.map(htsStatsBound -> GraResultsMapping.fromStatsGenderCountToStatsPreGenderHtBound(htsStatsBound._1, 
                                                                                        htsStatsBound._2,from,to)));
        }
    }
    
    
    private static GraConsumerProperties loadProps(String confsPath) throws Exception{
        GraConsumerProperties twProps=ConfigFactory.create(GraConsumerProperties.class);
        
        if(twProps==null){
            Properties props = Utils.Load.loadPropertiesFromPath(confsPath);
            twProps = ConfigFactory.create(GraConsumerProperties.class, props);
        }
        
        return twProps;
    }
    
}
