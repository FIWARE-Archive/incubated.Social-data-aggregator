package com.tilab.ca.sda.gra_core.ml;

import com.tilab.ca.sda.gra_core.GenderTypes;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.feature.IDF;
import org.apache.spark.mllib.feature.IDFModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;


public class FeaturesExtractionTFIDF extends FeatureExtractionTF implements FeaturesExtraction{
    
    private IDFModel idfModel;

    public FeaturesExtractionTFIDF(int numFeatures) {
        super(numFeatures);
    }
    
    @Override
    public JavaRDD<LabeledPoint> generateTrainingSet(JavaSparkContext jsc,String trainingFilePath){
        JavaRDD<LabeledPoint> tupleData =super.generateTrainingSet(jsc, trainingFilePath);
         JavaRDD<Vector> hashedData = tupleData.map(label -> label.features());
         idfModel = new IDF().fit(hashedData);
         JavaRDD<Vector> idf = idfModel.transform(hashedData);
         return idf.zip(tupleData).map(t -> new LabeledPoint(t._2.label(), t._1));
    }
    
    @Override
    public JavaRDD<LabeledPoint> generateFeatureExtractorLabeledPoints(JavaRDD<List<String>> wordsLstRdd){
        JavaRDD<LabeledPoint> tfLabels=super.generateFeatureExtractorLabeledPoints(wordsLstRdd);
        tfLabels.cache();
        JavaRDD<Vector> hashedData = tfLabels.map(label -> label.features());
        JavaRDD<Vector> idf = idfModel.transform(hashedData);
        
        return idf.map((idfVector) -> new LabeledPoint(GenderTypes.UNKNOWN.toLabel(), idfVector));
    }
    
}
