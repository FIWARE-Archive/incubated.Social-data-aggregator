package com.tilab.ca.sda.gra_core.components;

import com.tilab.ca.sda.ctw.utils.Utils;
import com.tilab.ca.sda.gra_core.GenderTypes;
import com.tilab.ca.sda.gra_core.GenderUid;
import com.tilab.ca.sda.gra_core.ml.FeaturesExtraction;
import com.tilab.ca.sda.gra_core.ml.MlModel;
import com.tilab.ca.sda.sda.model.TwUserProfile;
import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;


public class GRA implements Serializable{
    
    private final GenderUserDescr genderUserDescr;
    private final GenderUserColors genderUserColor;
    private final GenderNameSN genderName;
    

    public GRA(GRAConfig conf,JavaSparkContext jsc) {
        if(!conf.areMandatoryFieldsFilled())
            throw new IllegalStateException("Missing required data in GraConfig");
        //init all the sub algorithms 
        genderUserDescr=new GenderUserDescr(conf.descrModel, conf.fe, jsc, conf.trainingPathStr);
        genderUserColor=new GenderUserColors(conf.numBits, conf.numColors, conf.coloursModel, jsc, conf.trainingPathStr);
        genderName=new GenderNameSN(conf.namesGenderMap);
    }
    
    public JavaRDD<GenderUid> getGenderFromTwProfilesRDD(JavaRDD<TwUserProfile> twProfilesRdd){
        JavaRDD<GenderTypes> namesGenderRDD=twProfilesRdd.map(twProfile -> 
                genderName.getGenderFromNameScreenName(twProfile.getName(), twProfile.getScreenName()));
        
        JavaRDD<GenderTypes> coloursGenderRDD=twProfilesRdd.map(genderUserColor::getGenderFromProfileColours);
        JavaRDD<GenderUid> genderDescrRdd= genderUserDescr.getGendersFromTwProfiles(twProfilesRdd);
        //TODO parte in cui vengono raggruppati i risultati e dato il responso
        return null;
    }
    
    public static class GRAConfig{
    
        private MlModel coloursModel;
        private MlModel descrModel;
        private NamesGenderMap namesGenderMap;
        private FeaturesExtraction fe;
        private String trainingPathStr;
        private int numBits;
        private int numColors;
        
        public GRAConfig coloursClassifierModelClass(String coloursModelClassImpl) throws Exception{
            this.coloursModel=Utils.Load.getClassInstFromInterface(MlModel.class,coloursModelClassImpl,null);
            return this;
        }
        
        public GRAConfig descrClassifierModel(String descrModelClassImpl) throws Exception{
            this.descrModel=Utils.Load.getClassInstFromInterface(MlModel.class,descrModelClassImpl,null);
            return this;
        }
        
        public GRAConfig fearureExtractorImpl(String featureExtractorClassImpl) throws Exception{
            this.fe=Utils.Load.getClassInstFromInterface(FeaturesExtraction.class,featureExtractorClassImpl,null);
            return this;
        }
        
        public GRAConfig trainingPath(String trainingPathStr) throws Exception{
            this.trainingPathStr=trainingPathStr;
            return this;
        }
        
        public GRAConfig namesGenderMap(NamesGenderMap namesGenderMap) throws Exception{
            this.namesGenderMap=namesGenderMap;
            return this;
        }
        
        public GRAConfig numColorBitsMapping(int nbits){
            this.numBits=nbits;
            return this;
        }
        
        public GRAConfig numColorsMapping(int ncols){
            this.numColors=ncols;
            return this;
        }

        public boolean areMandatoryFieldsFilled(){
            return StringUtils.isNotBlank(trainingPathStr) && coloursModel!=null 
                    && descrModel!=null && fe!=null && namesGenderMap!=null
                    && numBits>0 && numColors>0;
        }
        
    }
}
