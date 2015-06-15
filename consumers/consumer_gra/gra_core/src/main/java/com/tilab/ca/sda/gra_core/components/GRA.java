package com.tilab.ca.sda.gra_core.components;

import com.tilab.ca.sda.ctw.utils.Utils;
import com.tilab.ca.sda.gra_core.GenderTypes;
import com.tilab.ca.sda.gra_core.GenderUid;
import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.gra_core.ml.FeaturesExtraction;
import com.tilab.ca.sda.gra_core.ml.MlModel;
import com.tilab.ca.sda.sda.model.TwUserProfile;
import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;


public class GRA implements Serializable{
    
    private static final Logger log=Logger.getLogger(GRA.class);
    
    private final GenderUserDescr genderUserDescr;
    private final GenderUserColors genderUserColor;
    private final GenderNameSN genderName;
    private final JavaSparkContext jsc;

    public GRA(GRAConfig conf,JavaSparkContext jsc) {
        if(!conf.areMandatoryFieldsFilled())
            throw new IllegalStateException("Missing required data in GraConfig");
        //init all the sub algorithms 
        genderUserDescr=new GenderUserDescr(conf.descrModel, conf.fe, jsc, conf.trainingPathStr);
        genderUserColor=new GenderUserColors(conf.numBits, conf.numColors, conf.coloursModel, jsc, conf.trainingPathStr);
        genderName=new GenderNameSN(conf.namesGenderMap);
        this.jsc=jsc;
    }
    
    
    public JavaRDD<ProfileGender> waterfallGraEvaluation(JavaRDD<TwUserProfile> twProfilesRdd){
        log.info("getting gender from name and screenName..");
        JavaRDD<ProfileGender> namesGenderRDD=twProfilesRdd.map(twProfile -> 
                new ProfileGender(twProfile,genderName.getGenderFromNameScreenName(twProfile.getName(), twProfile.getScreenName())));
        
        //filter profiles that are not recognized from the first algorithm
        log.info("getting gender from description..");
        JavaRDD<ProfileGender> notReconFromGender=namesGenderRDD.filter(profileGender -> profileGender.getGender()==GenderTypes.UNKNOWN ||
                                                                                         profileGender.getGender()==GenderTypes.AMBIGUOUS);
        JavaRDD<ProfileGender> descrGenderRdd=genderUserDescr.getGendersFromTwProfiles(notReconFromGender);
        
        JavaRDD<ProfileGender> notReconFromDescr=descrGenderRdd.filter(profileGender -> profileGender.getGender()==GenderTypes.UNKNOWN);
        
        log.info("getting gender from colors..");
        JavaRDD<ProfileGender> colorGenderRdd=genderUserColor.getGendersFromTwProfiles(notReconFromDescr);
        namesGenderRDD=namesGenderRDD.filter(profileGender -> profileGender.getGender()!=GenderTypes.UNKNOWN &&
                                                              profileGender.getGender()!=GenderTypes.AMBIGUOUS);
        descrGenderRdd=descrGenderRdd.filter(profileGender -> profileGender.getGender()!=GenderTypes.UNKNOWN);
        
        return jsc.union(namesGenderRDD,descrGenderRdd,colorGenderRdd);
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
