package com.tilab.ca.sda.gra_core.ml;

import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.MLUtils;


public class NBModel implements MlModel{
    
    private static final Logger log=Logger.getLogger(NBModel.class);
    
    private NaiveBayesModel model = null;

    public NBModel() {}
    public NBModel(Properties props) {}
    
    
    
    @Override
    public void init(JavaSparkContext jsc, String trainingFilePath) {
        log.info("Training file path is: "+trainingFilePath);
        log.info("Loading data lisvm format for training set...");
        JavaRDD<LabeledPoint> trainingData = MLUtils.loadLibSVMFile(jsc.sc(), trainingFilePath).toJavaRDD();
        log.info("init naive bayes classifier..");
        init(trainingData);
    }
    
    @Override
    public void init(JavaRDD<LabeledPoint> labeledPointsTrainingData) {
        model=NaiveBayes.train(labeledPointsTrainingData.rdd(), 1.0);
    }

    @Override
    public double predict(Vector features) {
        return model.predict(features);
    }
    
}
