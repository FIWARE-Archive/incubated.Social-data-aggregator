package com.tilab.ca.sda.gra_core.ml;

import java.io.Serializable;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.regression.LabeledPoint;


public interface FeaturesExtraction extends Serializable{
    
    public JavaRDD<LabeledPoint> generateTrainingSet(JavaSparkContext jsc,String trainingFilePath);
    
    public JavaRDD<LabeledPoint> generateFeatureExtractorLabeledPoints(JavaRDD<List<String>> wordsLstRdd);
}
