package com.tilab.ca.sda.gra_consumer_batch.mock;

import com.tilab.ca.sda.gra_core.ml.FeaturesExtraction;
import java.util.LinkedList;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;

/**
 *
 * @author dino
 */
public class FeatureExtractorTest implements FeaturesExtraction{

    private final JavaRDD<LabeledPoint> lpRdd;

    public FeatureExtractorTest() {
        lpRdd=null;
    }
    
    public FeatureExtractorTest(int numLp,JavaSparkContext sc) {
        List<LabeledPoint> lpList=new LinkedList<>();
        double[] vectContent={0f,1f};
        for(int i=0;i<numLp;i++)
            lpList.add(new LabeledPoint(0f, Vectors.dense(vectContent)));
        lpRdd=sc.parallelize(lpList);
    }

    @Override
    public JavaRDD<LabeledPoint> generateTrainingSet(JavaSparkContext jsc, String trainingFilePath) {
        return null;
    }

    @Override
    public JavaRDD<LabeledPoint> generateFeatureExtractorLabeledPoints(JavaRDD<List<String>> wordsLstRdd) {
        return lpRdd;
    }
    
}
