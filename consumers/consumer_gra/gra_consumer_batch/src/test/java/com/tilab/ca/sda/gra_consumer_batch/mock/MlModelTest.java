package com.tilab.ca.sda.gra_consumer_batch.mock;

import com.tilab.ca.sda.gra_core.ml.MlModel;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;


public class MlModelTest implements MlModel{

    private final double[] predictions;
    private int predictionIndex;
    
    public MlModelTest(double[] predictions) {
        this.predictions=predictions;
        predictionIndex=0;
    }

    
    @Override
    public void init(JavaRDD<LabeledPoint> labeledPointsTraining) {
        System.out.println("called init => labeledPoints training");
    }

    @Override
    public void init(JavaSparkContext sc, String trainingPath) {
        System.out.println("called init => javaSparkContext + trainingPath");
    }

    @Override
    public double predict(Vector features) {
        if(predictionIndex>=predictions.length)
            throw new IllegalStateException("Called predict more time than the prediction given in the creation phase");
        return predictions[predictionIndex++];
    }
    
}
