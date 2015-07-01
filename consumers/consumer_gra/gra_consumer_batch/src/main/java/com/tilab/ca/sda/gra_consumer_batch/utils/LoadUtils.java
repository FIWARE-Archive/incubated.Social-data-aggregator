package com.tilab.ca.sda.gra_consumer_batch.utils;

import com.tilab.ca.sda.ctw.utils.Utils;
import com.tilab.ca.sda.gra_consumer_dao.GraConsumerDao;
import com.tilab.ca.sda.gra_core.components.NamesGenderMap;
import com.tilab.ca.sda.gra_core.ml.FeaturesExtraction;
import com.tilab.ca.sda.gra_core.ml.MlModel;
import java.io.File;
import java.util.Properties;


public class LoadUtils {
    
    private static final String DAO_IMPL_CONF_FILE_NAME="dao_impl.conf";
    private static final String COLOUR_MLMODEL_IMPL_CONF_FILE_NAME="colour_mlmodel_impl.conf";
    private static final String DESCR_MLMODEL_IMPL_CONF_FILE_NAME="descr_mlmodel_impl.conf";
    private static final String FE_IMPL_CONF_FILE_NAME="features_extraction_impl.conf";
    private static final String NAMES_GENDER_IMPL_CONF_FILE_NAME="names_gender_mapping_impl.conf";
    
    public static GraConsumerDao loadConsumerGraDao(String confsPath,String implClassStr) throws Exception {
        Properties props=Utils.Load.loadPropertiesFromPath(confsPath+File.separator+DAO_IMPL_CONF_FILE_NAME);
        props.put(GraConsumerDao.CONF_PATH_PROPS_KEY, confsPath);
        return Utils.Load.getClassInstFromInterface(GraConsumerDao.class, implClassStr, props);
    }
    
    public static MlModel loadColourClassifierModel(String confsPath,String implClassStr) throws Exception{
        return loadFromFileProps(confsPath, implClassStr, MlModel.class,COLOUR_MLMODEL_IMPL_CONF_FILE_NAME);
    }
    
    public static MlModel loadDescrClassifierModel(String confsPath,String implClassStr) throws Exception{
        return loadFromFileProps(confsPath, implClassStr, MlModel.class,DESCR_MLMODEL_IMPL_CONF_FILE_NAME);
    }
    
    public static FeaturesExtraction loadDescrFeatureExtraction(String confsPath,String implClassStr) throws Exception{
        return loadFromFileProps(confsPath, implClassStr, FeaturesExtraction.class,FE_IMPL_CONF_FILE_NAME);
    }
    
     public static NamesGenderMap loadNamesGenderMap(String confsPath,String implClassStr) throws Exception{
        return loadFromFileProps(confsPath, implClassStr, NamesGenderMap.class,NAMES_GENDER_IMPL_CONF_FILE_NAME);
    }
                                             
                                            
     public static <T> T loadFromFileProps(String confsPath,String implClassStr,Class<T> interf,String fname) throws Exception{
        Properties props=Utils.Load.loadPropertiesFromPath(confsPath+File.separator+fname);
        return Utils.Load.getClassInstFromInterface(interf, implClassStr, props);
     }
}
