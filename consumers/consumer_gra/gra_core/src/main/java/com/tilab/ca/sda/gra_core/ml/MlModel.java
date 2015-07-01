package com.tilab.ca.sda.gra_core.ml;

import java.io.Serializable;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;

public interface MlModel extends Serializable{
    
    public void init(JavaRDD<LabeledPoint> labeledPointsTraining);
    
    public void init(JavaSparkContext sc,String trainingPath);
    
    public double predict(Vector features);
}
