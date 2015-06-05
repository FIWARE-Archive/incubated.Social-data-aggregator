package com.tilab.ca.sda.gra_core.components;

import com.tilab.ca.sda.gra_core.ml.FeaturesExtraction;
import com.tilab.ca.sda.gra_core.ml.MlModel;
import java.io.Serializable;
import org.apache.spark.api.java.JavaSparkContext;


public class GenderUserDescr implements Serializable{
    
    public GenderUserDescr(MlModel cmodel,FeaturesExtraction fe,JavaSparkContext sc,String trainingPath){
        
    }
}
