
package com.tilab.ca.sda.gra_core.ml;

import com.tilab.ca.sda.gra_core.GenderTypes;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.MLUtils;


public class FeatureExtractionTF implements FeaturesExtraction{
    
    private final HashingTF tf;
    
    public FeatureExtractionTF(int numFeatures){
        tf=new HashingTF(numFeatures);
    }
    
    @Override
    public JavaRDD<LabeledPoint> generateTrainingSet(JavaSparkContext jsc,String trainingFilePath){
        return  MLUtils.loadLibSVMFile(jsc.sc(), trainingFilePath).toJavaRDD();
    }
    
    @Override
    public JavaRDD<LabeledPoint> generateFeatureExtractorLabeledPoints(JavaRDD<List<String>> wordsLstRdd){
        return wordsLstRdd.map((lstStr) -> new LabeledPoint(GenderTypes.UNKNOWN.toLabel(), tf.transform(lstStr)));
    }
}
