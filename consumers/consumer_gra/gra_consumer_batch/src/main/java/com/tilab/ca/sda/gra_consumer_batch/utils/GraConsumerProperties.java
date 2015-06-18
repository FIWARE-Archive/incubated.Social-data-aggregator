package com.tilab.ca.sda.gra_consumer_batch.utils;

import com.tilab.ca.sda.gra_core.utils.GraConstants;
import org.aeonbits.owner.Config;

@Config.Sources({
	"file:${"+GraConstants.SDA_CONF_SYSTEM_PROPERTY+"}/${"+GraConstants.GRA_SYSTEM_PROPERTY+"}/GraConsumer.properties"
})
public interface GraConsumerProperties extends Config{
    
    
    @DefaultValue("3")
    public int roundPos();
    
    public String daoImplClass();
    
    //implementation of colours machine learning Model (Naive Bayes,Decision Tree...)
    
    @DefaultValue("com.tilab.ca.sda.gra_core.ml.NBModel")
    public String coloursModelImplClass();
    
    @DefaultValue("com.tilab.ca.sda.gra_core.ml.NBModel")
    public String descrModelImplClass();
    
    @DefaultValue("com.tilab.ca.sda.gra_core.components.NamesGenderMapDefaultImpl")
    public String namesGenderMapImplClass();
    
    @DefaultValue("com.tilab.ca.sda.gra_core.ml.FeaturesExtractionTFIDF")
    public String featureExtractionClassImpl();
    
    
    public String trainingFilesPath();
    
    @DefaultValue("9")
    public int colorAlgoReductionNumBits();
    
    /**
     * Number of colours to consider for gender classification through profile colours
     */
    @DefaultValue("4")
    public int colorAlgoNumColorsToConsider();
    
}
