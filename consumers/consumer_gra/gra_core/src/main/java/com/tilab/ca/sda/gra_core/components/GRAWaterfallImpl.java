package com.tilab.ca.sda.gra_core.components;

import com.tilab.ca.sda.gra_core.DescrResults;
import com.tilab.ca.sda.gra_core.GenderTypes;
import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.sda.model.TwUserProfile;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;


public class GRAWaterfallImpl implements GRA{
   
    private static final Logger log=Logger.getLogger(GRAWaterfallImpl.class);
    
    private GenderUserDescr genderUserDescr;
    private GenderUserColors genderUserColor;
    private GenderNameSN genderName;
    private JavaSparkContext jsc;
    
    private boolean initialized=false;

   
    @Override
    public void init(GRAConfig conf, JavaSparkContext jsc) {
        if(!conf.areMandatoryFieldsFilled())
            throw new IllegalStateException("Missing required data in GraConfig");
        //init all the sub algorithms 
        genderUserDescr=new GenderUserDescr(conf.getDescrModel(), conf.getFe(), jsc, conf.getTrainingPathStr());
        genderUserColor=new GenderUserColors(conf.getNumBits(), conf.getNumColors(), conf.getColoursModel(), jsc, conf.getTrainingPathStr());
        genderName=new GenderNameSN(conf.getNamesGenderMap());
        this.jsc=jsc;
        initialized=true;
    }
    
    @Override
    public JavaRDD<ProfileGender> evaluateProfiles(JavaRDD<TwUserProfile> twProfilesRdd){
        log.info("getting gender from name and screenName..");
        
        if(!initialized)
            throw new IllegalStateException("GRA implementation not initialized. Please call init() method before evaluation.");
        
        //
        JavaRDD<ProfileGender> namesGenderRDD=genderName.getNamesGenderRDD(twProfilesRdd);
        
        //System.out.println("*************************************************************");
        //namesGenderRDD.collect().forEach(pg ->System.out.println(pg.getTwProfile().getName()+" "+pg.getGender().toChar()));
        //System.out.println("*************************************************************");
        
        //filter profiles that are not recognized from the first algorithm
        JavaRDD<ProfileGender> notReconFromName=namesGenderRDD.filter(profileGender -> profileGender.getGender()==GenderTypes.UNKNOWN ||
                                                                                         profileGender.getGender()==GenderTypes.AMBIGUOUS);
        
        //System.out.println("Not recognized*************************************************************");
        //notReconFromName.collect().forEach(pg ->System.out.println(pg.getTwProfile().getName()+" "+pg.getGender().toChar()));
        //System.out.println("*************************************************************");
        
        
        log.info("getting gender from description..");
        DescrResults descrResults=genderUserDescr.getGendersFromTwProfiles(notReconFromName);
        JavaRDD<ProfileGender> descrGenderRdd=descrResults.getProfilesRecognized();
               
        JavaRDD<ProfileGender> notReconFromDescr=descrResults.getProfilesUnrecognized().filter(profileGender -> profileGender.getGender()==GenderTypes.UNKNOWN);
        
        log.info("getting gender from colors..");
        JavaRDD<ProfileGender> colorGenderRdd=genderUserColor.getGendersFromTwProfiles(notReconFromDescr);
        
        namesGenderRDD=namesGenderRDD.filter(profileGender -> profileGender.getGender()!=GenderTypes.UNKNOWN &&
                                                               profileGender.getGender()!=GenderTypes.AMBIGUOUS);
        
        return jsc.union(namesGenderRDD,descrGenderRdd,colorGenderRdd);
    }

}
