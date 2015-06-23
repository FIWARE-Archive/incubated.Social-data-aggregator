package com.tilab.ca.sda.gra_core.components;

import com.tilab.ca.sda.gra_core.DescrResults;
import com.tilab.ca.sda.gra_core.GenderTypes;
import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.gra_core.ProfileDescrLst;
import com.tilab.ca.sda.gra_core.ml.FeaturesExtraction;
import com.tilab.ca.sda.gra_core.ml.MlModel;
import com.tilab.ca.sda.gra_core.utils.GraConstants;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;


public class GenderUserDescr implements Serializable{
    
    private final MlModel model;
    private final FeaturesExtraction fe;
    private final List<String> smiles;
    private final List<String> stopWords;
    private static final int MIN_WORD_LENGTH=2;
    
    
    public GenderUserDescr(MlModel cmodel,FeaturesExtraction fe,JavaSparkContext sc,String trainingPath){
        this.model=cmodel;
        this.fe=fe;
        model.init(fe.generateTrainingSet(sc, trainingPath+File.separator+GraConstants.DESCR_TAG+GraConstants.TRAINING_FILE_NAME));
        //loading allowed smiles
        smiles=sc.textFile(trainingPath+File.separator+GraConstants.DESCR_SMILES).collect();
        //loading stop words
        stopWords=sc.textFile(trainingPath+File.separator+GraConstants.DESCR_STOP_WORDS).collect();
    }
    
    /**
     * Process the description of a user profile cleaning from urls refs numbers mentions and hts.
     * @param description user profile description to process
     * @return 
     */
    public static String processDescriptionText(String description) {
                if(StringUtils.isBlank(description))
                    return description;
                
		return description.replaceAll("(https?:\\/\\/[^\\s]+)", "")
				.replaceAll("(www\\.[^\\s]+)", "") //remove urls references
				.replaceAll("[0-9]+", "")
                                .replaceAll("@[^\\s]+", "") //remove mentions
				.replaceAll("#", " ") //remove hash from hashtags
				.replaceAll("[\\s]+", " ") //remove multiple spaces
				.trim().toLowerCase();
    }
    
    /**
     * Check if the word passed as parameter is a smile or not (from the smiles list file provided)
     * @param word
     * @return 
     */
    private boolean isASmile(String word){
        return smiles.contains(word);
    }
    
    /**
     * Clear the word from any non alphabetic character
     * @param word
     * @return 
     */
    public String cleanWord(String word){
        return word.replaceAll("[\\W_]", "");
    }
    
    /**
     * Check if the word passed as parameter satisfy length requirements and it is contained or not in a list of stopwords.
     * If it is contained the word is not good as key.
     * @param word
     * @return 
     */
    public boolean isOkAsKey(String word){
        return word.length()>MIN_WORD_LENGTH && !stopWords.contains(word);
    }
    
    
    /**
     * 
     * @param description
     * @return 
     */
    public List<String> processDescription(String description){
        List<String> descrWords=new ArrayList<>();
        
        if(StringUtils.isNotBlank(description)){
            String descrClean=processDescriptionText(description);
            String[] words=descrClean.split(" ");
            String clWord;
            for(String word:words){
                if(!isASmile(word)){
                    clWord=cleanWord(word);
                    if(isOkAsKey(clWord))
                        descrWords.add(clWord);
                }else{
                    descrWords.add(word);
                }
            }
        }
        return descrWords;    
    }
    
    public DescrResults getGendersFromTwProfiles(JavaRDD<ProfileGender> profilesRDD){
        
        //profiles that does not provide enough information
        JavaRDD<ProfileGender> undefinedProfiles=profilesRDD.filter((twProfileGender) -> processDescription(twProfileGender.getTwProfile().getDescription()).isEmpty())
                                                        .map(twProfileGender -> new ProfileGender(twProfileGender.getTwProfile(),GenderTypes.UNKNOWN));
        
        
        //get only profiles with a good description and map to an intermediate state (uid,list cleaned word in description)
        JavaRDD<ProfileDescrLst> definedProfiles=profilesRDD
                .map(twProfileGender -> new ProfileDescrLst(twProfileGender.getTwProfile(),
                                                            processDescription(twProfileGender.getTwProfile().getDescription())))
                .filter((uidDescr) -> !uidDescr.getDescrStrLst().isEmpty());
        
        //cache rdd
        definedProfiles.cache();
        
        //use the model to classify genders
        JavaRDD<GenderTypes> gtsRDD=fe.generateFeatureExtractorLabeledPoints(definedProfiles.map(profileDescr -> profileDescr.getDescrStrLst()))
                                      .map(lp -> GenderTypes.fromLabel(model.predict(lp.features())));
        
        //return the union of classified profiles with the ones unknown (not enough information in the description field)
        return  new DescrResults(gtsRDD.zip(definedProfiles).map(zippedRdd -> new ProfileGender(zippedRdd._2.getProfile(), zippedRdd._1)),
                                 undefinedProfiles);
    }
    
}
