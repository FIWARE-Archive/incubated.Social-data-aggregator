package com.tilab.ca.sda.gra_consumer_batch.utils;

import org.aeonbits.owner.Config;


public interface GraConsumerProperties extends Config{
    
    public String defaultRoundMode();
    
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
