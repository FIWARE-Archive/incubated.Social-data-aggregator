import re
from operator import add
import logging
from pyspark.mllib.feature import HashingTF,IDF
from pyspark.mllib.regression import LabeledPoint
import argparse
from pyspark import SparkContext
import utils

genderDict={'m':0.0,'f':1.0,'x':2.0}

def getListFromFile(filename):
    with open(filename,"rb") as sFp:
        return sFp.read().splitlines()

def getSmilesList():
    return getListFromFile("smiles.txt")
    
def getStopWordsList():
    return getListFromFile("stopWords.txt")

def getGenderDescriptionFromRow(row,s,smilesList,stopWordsList):
    fields=row.split(s)
    print fields
    return (fields[4],getWordKeysList(smilesList, stopWordsList, fields[3]))

def getWordKeysList(smilesList,stopWordsList,description):
    words=clearDescription(description).split(" ")
    return [cleanWord(smilesList, word) for word in words if isOkAsKey(stopWordsList, word)]

def clearDescription(descriptionText):
    #remove references to urls
    descriptionText=re.sub("(https?:\\/\\/[^\\s]+)", "", descriptionText)
    descriptionText=re.sub("(www\\.[^\\s]+)", "", descriptionText)
    #remove mentions  
    descriptionText=re.sub("@[^\\s]+", "", descriptionText)
    #remove numbers
    descriptionText=re.sub("[0-9]+", "", descriptionText)
    #remove \r and \n # and additional spaces
    descriptionText=re.sub("[\r\n#]+", " ", descriptionText)
    descriptionText=re.sub("[\\s]+", " ", descriptionText)
    return descriptionText.lower().strip()

def cleanWord(smileList,word):
    if word not in smileList:
        word=re.sub("[^\\w]+", " ", word)   
    return word.strip()

def isOkAsKey(stopWordsList,word):
    return word and len(word) >=3 and word not in stopWordsList  

def generate_gender_tf(twProfilesRdd,numFe):
    """
    Generate Term Frequency tuple (gender,sparse vector) from rdd containing following tuples:
    (gender,(clean words tuple))
    """
    tf = HashingTF(numFeatures = numFe)
    return twProfilesRdd.map(lambda genderDescrTuple: (genderDict[genderDescrTuple[0]],tf.transform(genderDescrTuple[1])))

def generate_tf_idf_training(twProfilesRdd,numFe):
    """
    Generate TF IDF tuple (gender,sparse vector) from rdd containing following tuples:
    (gender,(clean words tuple))
    """
    gtlp=generate_gender_tf(twProfilesRdd, numFe)
    idf=IDF()
    tfVectorsRDD=gtlp.map(lambda tp: tp[1])
    idfModel=idf.fit(tfVectorsRDD)
    idfRdd=idfModel.transform(tfVectorsRDD)
    #print idfRdd.zip(gtlp).collect()
    return (idfRdd.zip(gtlp).map(lambda tp:(tp[1][0],tp[0])),idfModel)
    
def generate_tf_idf(profilesRdd,numFe,idfModel):
    gtlp=generate_gender_tf(profilesRdd, numFe)
    tfVectorsRDD=gtlp.map(lambda tp: tp[1])
    idfRdd=idfModel.transform(tfVectorsRDD)
    return idfRdd.zip(gtlp).map(lambda tp:(tp[1][0],tp[0]))
    
    
def saveWordIndexes(userWordsIdsDict):
    with open("userWordsIds.txt","w") as cFp:
        for word,index in userWordsIdsDict.iteritems():
            cFp.write("%d,%s\n" %(index,word))

def log_word_results(genderWordList,gender):
    logging.info("WORDS:")
    logging.info("Stats for gender %s" % gender)
    for wordGenderTuple in genderWordList:
        logging.info("{0} appear {1} times".format(wordGenderTuple[0][1],wordGenderTuple[1]))
        
def log_descr_res_gender(userGenderWordCountRdd,gender):
    userGenderWordTotG=userGenderWordCountRdd.filter(lambda pairTuple: pairTuple[0][0]==gender and pairTuple[1] > 1 and pairTuple[0][1]).collect()
    userGenderWordTotG=sorted(userGenderWordTotG,key=lambda pairTuple:pairTuple[1],reverse=True)
    userGenderWordTotG=userGenderWordTotG[:15]
    log_word_results(userGenderWordTotG, gender)


def generate_stats_on_data_user_description(twProfilesRdd,s,smilesList,stopWordsList):
    '''
    @param s: char separator
    @param twProfilesRdd: rdd containing raw tw_user_profiles
    '''
    userGenderDescriptionRdd=twProfilesRdd.map(lambda row: getGenderDescriptionFromRow(row, s,smilesList,stopWordsList))
    
    userGenderDescriptionRdd.cache()
    
    userGenderWordCountRdd=userGenderDescriptionRdd.flatMap(lambda genderWordsTuple: [((genderWordsTuple[0],word),1) for word in genderWordsTuple[1]]) \
                                                   .reduceByKey(add)
    log_descr_res_gender(userGenderWordCountRdd,'m')
    log_descr_res_gender(userGenderWordCountRdd,'f')
    log_descr_res_gender(userGenderWordCountRdd,'x')
    
def generate_sets_and_evaluate_algorithms_user_descr(trainingRdd,testRdd,numFeatures,algType):
    logging.info("try with {0} algorithm and evaluate scores with numFeatures = {1}...".format(algType,numFeatures))
    trainingRdd,testRdd=tf_IDF_switch(trainingRdd,testRdd,numFeatures,algType)
    print "starting with naive bayes!!"
    utils.evaluate_naive_bayes(trainingRdd, testRdd, 1.0)
    #utils.evaluate_decision_tree(trainingRdd, testRdd)
    #utils.evaluate_random_forest(trainingRdd, testRdd)
    utils.evaluate_majority_svm(trainingRdd, testRdd)

def tf_IDF_switch(trainingRdd,testRdd,numFeatures,algType):
    if algType=="tf":
        trainingRdd=generate_gender_tf(trainingRdd, numFeatures).map(lambda tp:LabeledPoint(tp[0],tp[1]))
        testRdd=generate_gender_tf(testRdd, numFeatures).map(lambda tp:LabeledPoint(tp[0],tp[1]))
    else:
        print "************************************************************************"
        print "generating tf-idf training set.."
        trainingRdd,idfModel=generate_tf_idf_training(trainingRdd, numFeatures)
        trainingRdd=trainingRdd.map(lambda tp:LabeledPoint(tp[0],tp[1]))
        print "************************************************************************"
        print "generating tf-idf test set.."
        testRdd=generate_tf_idf(testRdd, numFeatures,idfModel).map(lambda tp:LabeledPoint(tp[0],tp[1]))
        print "************************************************************************"
        #print testRdd.collect()
        print "DONE!!"
    return (trainingRdd,testRdd)


def evaluate_descr_alg(trainingRDDRaw,testRDDRaw,algName,log2numFeatures):
    logging.info("==========================================")
    logging.info("{0} EVALUATION".format(algName))
    logging.info("==========================================")
    #for log2numFeatures in range(16,21):
    generate_sets_and_evaluate_algorithms_user_descr(trainingRDDRaw,testRDDRaw,pow(2,log2numFeatures),algName)

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='extract features from tw_profiles')
    
    parser.add_argument('--all', help='one file that will be divided in training and test')
    parser.add_argument('--test', help='input path where training tw_profiles data are stored')
    parser.add_argument('--training', help='input path where training tw_profiles data are stored')
    parser.add_argument('--nf', help='log2 num features (expressed as a power of 2)')
    
    logging.basicConfig(filename='user_descr.log',level=logging.INFO)
    smilesList=getSmilesList()
    stopWordsList=getStopWordsList()
    
    print "smiles:"
    print smilesList
    print "stop words:"
    print stopWordsList
    
    #,pyFiles=pyFls
    sc = SparkContext(appName="TwProfilesUserDescr",pyFiles=utils.modules_to_load(['utils.py'])) 
    args = parser.parse_args()
    
    if args.all:
        allProfiles=sc.textFile(args.all)
        (trainingRDDRaw,testRDDRaw)=allProfiles.randomSplit([0.7,0.3])
        print "saving training set.."
        trainingRDDRaw.saveAsTextFile("/tmp/descr_training_set")
        print "saving test set.."
        testRDDRaw.saveAsTextFile("/tmp/descr_test_set")
    elif args.test and args.training:
        trainingRDDRaw=sc.textFile(args.training)
        testRDDRaw=sc.textFile(args.test)
    else:
        raise RuntimeError("you must provide an argument chosing between all or training and test")    
    log2numFeatures=int(args.nf)
    s='\x01'
    
    #generate_stats_on_data_user_description(trainingRDDRaw,s,smilesList,stopWordsList)
    #generate_stats_on_data_user_description(testRDDRaw,s,smilesList,stopWordsList)
    
    trainingRDDRaw=trainingRDDRaw.map(lambda row: getGenderDescriptionFromRow(row, s,smilesList,stopWordsList))
    testRDDRaw=testRDDRaw.map(lambda row: getGenderDescriptionFromRow(row, s,smilesList,stopWordsList))
    
    evaluate_descr_alg(trainingRDDRaw,testRDDRaw,"tf",log2numFeatures)
    evaluate_descr_alg(trainingRDDRaw,testRDDRaw,"tf-idf",log2numFeatures)
    