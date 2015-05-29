from pyspark.mllib.classification import NaiveBayes
from pyspark.mllib.classification import LogisticRegressionWithLBFGS
from pyspark.mllib.tree import DecisionTree
from pyspark.mllib.tree import RandomForest
import itertools
import logging
from pyspark.sql.types import Row
import os
import urlparse
import urllib

def hex_to_rgb(value):
    """
    transform the hex input string to a rgb value tuple.
    for example #b6dada hex color will be converted into (182, 218, 218)
    @param value: the hex value for the color
    @return the rgb tuple for the input color
    """
    value = value.lstrip('#')
    lv = len(value)
    return tuple(int(value[i:i + lv // 3], 16) for i in range(0, lv, lv // 3))

def rgb_to_hex(rgb):
    """
    transform the rgb input tuple to a hex string.
    for example (182, 218, 218) rgb tuple will be converted into 'b6dada' hex color
    @param rgb: rgb tuple
    @return the hex value for the input rgb tuple
    """
    return '%02x%02x%02x' % rgb

def generate_palette_RGB(numBits):
    """
    generate all the combinations for rgb with the number 
    of bits passed as parameter
    @param numBits: number of bits for each channel
    @return a list of tuples containing all the possible combinations
    """
    print "there are %d possible combination with %d bits" %(pow(pow(2, numBits),3),numBits)
    return [combination for combination in itertools.product(xrange(pow(2, numBits)), repeat=3)] 
  

def change_RGB_bit_depth(rgbTuple,numBitsIn,numBitsOut):
    """
    converts x bit RGB color values to their closest equivalent in y bit depths.
    @param rgbTuple: rgb tuple in input
    @param numBitsIn: number of bits (in total) of RGB tuple in input
    @param numBitsOut: number of bits (in total) of RGB tuple in output
    @return a tuple containing the different channels with bit depth changed
    Example:
    rgbTuple=(192, 222, 237)
    res=change_RGB_bit_depth(rgbTuple,24,9)
    
    res -> (5, 6, 6)
    """
    maxValOut=pow(2,numBitsOut/3)-1
    maxValIn=pow(2,numBitsIn/3)-1
    return (int(rgbTuple[0]*maxValOut/maxValIn),
            int(rgbTuple[1]*maxValOut/maxValIn),
            int(rgbTuple[2]*maxValOut/maxValIn))

#######################
## MACHINE LEARNING ###
#######################

def majority_eval(modelmf,modelmx,modelfx,lp):
    """
    Evaluate by majority from the combination of binomial svm
    """
    lst=[0]*3
    lst[modelmf.predict(lp.features)]+=1
    lst[modelmx.predict(lp.features)*2]+=1
    lst[modelfx.predict(lp.features)+1]+=1
    return lst.index(max(lst))

def temp(genderPredicted,genderReal):
    print("predicted {0} real {1}".format(genderPredicted,genderReal))
    
def get_accuracy(predictionAndLabel,testRdd,classifierName):
    accuracy = 1.0 * predictionAndLabel.filter(lambda (genderPredicted, genderReal): genderPredicted == genderReal).count() / testRdd.count()
    logging.info("{0} overall accuracy is {1:.2f} %".format(classifierName,accuracy*100))
    maleAccuracy=1.0 * predictionAndLabel.filter(lambda (genderPredicted, genderReal): genderPredicted == genderReal and genderReal==0).count() / testRdd.filter(lambda lp:lp.label==0).count()
    logging.info("{0} accuracy over male is {1:.2f} %".format(classifierName,maleAccuracy*100))
    femaleAccuracy=1.0 * predictionAndLabel.filter(lambda (genderPredicted, genderReal): genderPredicted == genderReal and genderReal==1).count() / testRdd.filter(lambda lp:lp.label==1).count()
    logging.info("{0} accuracy over female is {1:.2f} %".format(classifierName,femaleAccuracy*100))
    pageAccuracy=1.0 * predictionAndLabel.filter(lambda (genderPredicted, genderReal): genderPredicted == genderReal and genderReal==2).count() / testRdd.filter(lambda lp:lp.label==2).count()
    logging.info("{0} accuracy over page is {1:.2f} %".format(classifierName,pageAccuracy*100))
    

def evaluate_naive_bayes(trainingRdd,testRdd,lambdaParam):
    model = NaiveBayes.train(trainingRdd, lambdaParam)
    # Make prediction and test accuracy.
    predictionAndLabel = testRdd.map(lambda p : (model.predict(p.features), p.label))
    get_accuracy(predictionAndLabel,testRdd,"Naive Bayes")
    #accuracy = 1.0 * predictionAndLabel.filter(lambda (genderPredicted, genderReal): genderPredicted == genderReal).count() / testRdd.count()
    #logging.info("Naive Bayes overall accuracy is {0:.2f} %".format(accuracy*100))
    #predictionAndLabel.foreach(lambda (genderPredicted, genderReal): temp(genderPredicted, genderReal))
    #maleAccuracy=1.0 * predictionAndLabel.filter(lambda (genderPredicted, genderReal): genderPredicted == genderReal and genderReal==0).count() / testRdd.filter(lambda lp:lp.label==0).count()
    #femaleAccuracy=1.0 * predictionAndLabel.filter(lambda (genderPredicted, genderReal): genderPredicted == genderReal and genderReal==1).count() / testRdd.filter(lambda lp:lp.label==1).count()
    #pageAccuracy=1.0 * predictionAndLabel.filter(lambda (genderPredicted, genderReal): genderPredicted == genderReal and genderReal==2).count() / testRdd.filter(lambda lp:lp.label==2).count()
    
    
def map_to_0_1_label(l):
    l.label=float(int(l.label/2))
    return l

def evaluate_majority_svm(trainingRdd,testRdd):
    modelmf = LogisticRegressionWithLBFGS.train(trainingRdd.filter(lambda l: l.label!=2)) #model with male and female
    modelmx = LogisticRegressionWithLBFGS.train(trainingRdd.filter(lambda l: l.label!=1).map(lambda l: map_to_0_1_label(l))) #model with male and pages
    modelfx = LogisticRegressionWithLBFGS.train(trainingRdd.filter(lambda l: l.label!=0).map(lambda l: map_to_0_1_label(l))) #model with female and pages
    
    labelsAndPreds = testRdd.map(lambda p: (majority_eval(modelmf,modelmx,modelfx,p),p.label))
    #accuracy = 1.0 * labelsAndPreds.filter(lambda (genderPredicted, genderReal): genderPredicted == genderReal).count() / testRdd.count()
    #logging.info("Logistic Regression accuracy is {0:.2f} %".format(accuracy*100))
    get_accuracy(labelsAndPreds,testRdd,"Logistic Regression")


def evaluate_decision_tree(trainingRdd,testRdd,maxDepth=5,maxBins=32,impurity='gini',cfi={}):
    logging.info("Decision tree with config params -> impurity={0}, maxDepth={1},maxBins={2}".format(impurity,maxDepth,maxBins))
    model = DecisionTree.trainClassifier(trainingRdd, numClasses=3,categoricalFeaturesInfo=cfi,
                                     impurity=impurity, maxDepth=5, maxBins=64)
    
    predictions = model.predict(testRdd.map(lambda x: x.features))
    labelsAndPredictions = testRdd.map(lambda lp: lp.label).zip(predictions)
    #accuracy = 1.0 * labelsAndPredictions.filter(lambda (genderPredicted, genderReal): genderPredicted == genderReal).count() / testRdd.count()
    #logging.info("Decision Tree accuracy is {0:.2f} %".format(accuracy*100))
    get_accuracy(labelsAndPredictions,testRdd,"Decision Tree")
    

def evaluate_random_forest(trainingRdd,testRdd,cfi={}):
    model = RandomForest.trainClassifier(trainingRdd, numClasses=3, categoricalFeaturesInfo=cfi,
                                     numTrees=8, featureSubsetStrategy="auto", #3
                                     impurity='gini', maxDepth=10, maxBins=64) #5 32
    
    predictions = model.predict(testRdd.map(lambda x: x.features))
    labelsAndPredictions = testRdd.map(lambda lp: lp.label).zip(predictions)
    #accuracy = 1.0 * labelsAndPredictions.filter(lambda (genderPredicted, genderReal): genderPredicted == genderReal).count() / testRdd.count()
    #logging.info("Random Forest accuracy is {0:.2f} %".format(accuracy*100))
    get_accuracy(labelsAndPredictions,testRdd,"Random Forest")
    print 'Learned classification forest model:'
    print model.toDebugString()

def t(row,s):
    print row.encode("utf8","ignore")
    print row.split(s)[6]
    return int(row.split(s)[6])==1
        
    
def show_total_stats(profilesRDD,name,s):
    """
        @param profilesRDD: rdd containing profiles
        @param name : name of the data type (training,test,total)
        @param s: file separator 
    """
    numMales=profilesRDD.filter(lambda row: row.split(s)[4]=="m").count()
    numFemales=profilesRDD.filter(lambda row: row.split(s)[4]=="f").count()
    numPages=profilesRDD.filter(lambda row: row.split(s)[4]=="x").count()
    numPeopleMantainingName=profilesRDD.filter(lambda row: int(row.split(s)[6])==1).count() #int(row.split(s)[6])==1
    percentagePeopleMantainingName=(float(numPeopleMantainingName)/float(numMales+numFemales))*100
    logging.info( "*****************************************************************************")
    logging.info( "STATISTICS ON {0}:".format(name))
    logging.info( "number of total profiles: %d" % profilesRDD.count())
    logging.info( "number of males profiles: %d" % numMales)
    logging.info( "number of females profiles: %d" % numFemales)
    logging.info( "number of pages profiles: %d" % numPages)
    logging.info( "numPeopleMantainingName: %d" % numPeopleMantainingName)
    logging.info( "percentage of profiles in which people mantaining their first name: %d" % percentagePeopleMantainingName)    
    logging.info( "*****************************************************************************")

def path2Url(path):
    return urlparse.urljoin('file:',urllib.pathname2url(path))  
    
def modules_to_load(modules2Load):
    pyFilesBaseDir=os.path.dirname(os.path.abspath(__file__))
    pyFls=[path2Url(os.path.join(pyFilesBaseDir,moduleName)) for moduleName in modules2Load]
    return pyFls