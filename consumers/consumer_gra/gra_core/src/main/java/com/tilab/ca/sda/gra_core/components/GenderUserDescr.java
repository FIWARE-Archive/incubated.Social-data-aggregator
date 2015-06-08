package com.tilab.ca.sda.gra_core.components;

import com.tilab.ca.sda.gra_core.GenderTypes;
import com.tilab.ca.sda.gra_core.GenderUid;
import com.tilab.ca.sda.gra_core.UidDescrLst;
import com.tilab.ca.sda.gra_core.ml.FeaturesExtraction;
import com.tilab.ca.sda.gra_core.ml.MlModel;
import com.tilab.ca.sda.gra_core.utils.GraConstants;
import com.tilab.ca.sda.sda.model.TwUserProfile;
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
    private static final int MIN_WORD_LENGTH=3;
    
    
    public GenderUserDescr(MlModel cmodel,FeaturesExtraction fe,JavaSparkContext sc,String trainingPath){
        this.model=cmodel;
        this.fe=fe;
        model.init(fe.generateTrainingSet(sc, trainingPath+GraConstants.DESCR_TAG+GraConstants.TRAINING_FILE_NAME));
        //loading allowed smiles
        smiles=sc.textFile(trainingPath+GraConstants.DESCR_SMILES).collect();
        //loading stop words
        stopWords=sc.textFile(trainingPath+GraConstants.DESCR_STOP_WORDS).collect();
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
        if(!isASmile(word))
                return word.replaceAll("[\\W_]", "");
        return word;
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
    
    public JavaRDD<GenderUid> getGendersFromTwProfiles(JavaRDD<TwUserProfile> profilesRDD){
        
        //profiles that does not provide enough information
        JavaRDD<GenderUid> undefinedProfiles=profilesRDD.filter((twProfile) -> processDescription(twProfile.getDescription()).isEmpty())
                                                        .map(twProfile -> new GenderUid(twProfile.getUid(),GenderTypes.UNKNOWN));
        
        //get only profiles with a good description and map to an intermediate state (uid,list cleaned word in description)
        JavaRDD<UidDescrLst> definedProfiles=profilesRDD
                .map(twProfile -> new UidDescrLst(twProfile.getUid(),processDescription(twProfile.getDescription())))
                .filter((uidDescr) -> !uidDescr.getDescrStrLst().isEmpty());
        
        //cache rdd
        definedProfiles.cache();
        
        //use the model to classify genders
        JavaRDD<GenderTypes> gtsRDD=fe.generateFeatureExtractorLabeledPoints(definedProfiles.map(uidDescr -> uidDescr.getDescrStrLst()))
                                      .map(lp -> GenderTypes.fromLabel(model.predict(lp.features())));
        
        //return the union of classified profiles with the ones unknown (not enough information in the description field)
        return gtsRDD.zip(definedProfiles).map(zippedRdd -> new GenderUid(zippedRdd._2.getUid(), zippedRdd._1))
                                          .union(undefinedProfiles);
    }
    
}
