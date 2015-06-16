package com.tilab.ca.sda.gra_consumer_dao;

import com.tilab.ca.sda.gra_consumer_dao.data.TwGenderProfile;
import java.io.File;
import java.util.Properties;
import org.apache.spark.api.java.JavaRDD;

public class GraConsumerDaoFileImpl implements GraConsumerDao{

    public static final String OUTPUT_FILES_PATH_PROP="graOutputFilesPath";
    public static final String OUTPUT_FILE_PROFILES_NAME="profiles_";
    
    private final String outputPathName;
    
    public GraConsumerDaoFileImpl(Properties props) {
        outputPathName=props.getProperty(OUTPUT_FILES_PATH_PROP);
    }

    @Override
    public void saveTwGenderProfiles(JavaRDD<TwGenderProfile> twGenderProfiles) {
        twGenderProfiles.saveAsTextFile(outputPathName+File.separator+OUTPUT_FILE_PROFILES_NAME+
                                        System.currentTimeMillis());
    }
    
}
