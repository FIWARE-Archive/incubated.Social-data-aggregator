#7-11
from operator import add
import logging
import utils
import argparse
from pyspark import SparkContext

from pyspark.mllib.regression import LabeledPoint
from pyspark.mllib.linalg import SparseVector

numBitsIn=24
genderDict={'m':0.0,'f':1.0,'x':2.0}

def get_gender_reduced_colors_from_row(row,s,numColFields,numBitsOut):
    """
    @param row: raw string containing the data
    @param  s: file separator
    @param numColFields: number of colour fields to take   
    @return a tuple containing (userGender, (profile colours reduced rgb),name) 
    where profile colours are:
     - background
     - text
     - link
     - sidebar fill
     - sidebar border
    """
    gcfrTuple=get_gender_colors_from_row(row,s,numColFields)
    return (gcfrTuple[0],get_RGB_tuple_depth_reduced_from_hex(gcfrTuple[1],numBitsOut),gcfrTuple[2])

def get_gender_colors_from_row(row,s,numColFields):
    """
    @param row: raw string containing the data
    @param  s: file separator
    @param numColFields: number of colour fields to take   
    @return a tuple containing (userGender, (profile colours hex),name) 
    where profile colours are:
     - background
     - text
     - link
     - sidebar fill
     - sidebar border
    """
    fields=row.split(s)
    return (fields[4],tuple(fields[7:(7+numColFields)]),fields[1]) #fields[4] = gender, fields[1]=username (for debug)

def save_color_indexes(userColorsIdsDict,numBits):
    """
     persist the dictionary of rgb colours, indexes on file (userColorsIds_<num_bits>.txt)
    """
    with open("gen/userColorsIds_{0}.txt".format(numBits),"w") as cFp:
        for colorTuple,index in userColorsIdsDict.iteritems():
            cFp.write("%d:%s\n" %(index,",".join(map(str,colorTuple))))

def get_RGB_tuple_depth_reduced_from_hex(hexTuple,numBitsOut):
    """
    
    """
    
    rgbTuples=tuple(utils.hex_to_rgb(val) for val in hexTuple)
    return tuple(utils.change_RGB_bit_depth(rgbTuple, numBitsIn, numBitsOut) for rgbTuple in rgbTuples)

def map_to_gender_dict_tuple(genderColorsTuple,rgbColDict,numBits):
    genderLabel=genderDict[genderColorsTuple[0]]
    currUserColorsDict={}
    for rgbCol in genderColorsTuple[1]:
        if rgbColDict[rgbCol] not in currUserColorsDict:
            currUserColorsDict[rgbColDict[rgbCol]]=0
        currUserColorsDict[rgbColDict[rgbCol]]+=1
    return (genderLabel,currUserColorsDict)

def map_to_labeled_point(genderDictTuple,numBits):
    return LabeledPoint(genderDictTuple[0],SparseVector(pow(2,numBits),genderDictTuple[1]))

def map_to_libsvm_format(genderDictTuple):
    spVectorList=["{0}:{1}".format(key,genderDictTuple[1][key]) for key in sorted(genderDictTuple[1].keys())]
    logging.debug("spVectorList is")
    logging.debug(spVectorList)
    return "{0} {1}".format(genderDictTuple[0]," ".join(spVectorList))

def save_training_set_libsvm_format(userGenderColorsDictRdd,numBits):
    userGenderColorsDictRdd.map(lambda genderColorsDictTuple: map_to_libsvm_format(genderColorsDictTuple)) \
                           .saveAsTextFile("gen/training_colour_libsvm_data_{0}".format(numBits))

def generate_and_save_rgb_col_dict(numBits,saveOnFile=True):
    print "generating rgb colors and indexes.."
    rgbColDict={val:index for index,val in enumerate(utils.generate_palette_RGB(numBits/3))}
    if saveOnFile:
        print "saving rgb color/indexes on file.."
        save_color_indexes(rgbColDict,numBits)
    
    return rgbColDict    

def generate_training_model(sc,trainingData,s,numBits,numColorFields,rgbColDict,saveOnFile=True):
    print "generate_training_model with {0} number of bits for all colours channel and {1} considered num columns".format(numBits,
                                                                                                                          numColorFields)
    print "mapping rgb colors on %d bits" % numBits
    userGenderColorsRdd=trainingData.map(lambda row: get_gender_reduced_colors_from_row(row, s,numColorFields,numBits)) 
    userGenderColorsDictRdd=userGenderColorsRdd.map(lambda genderColorsTuple: map_to_gender_dict_tuple(genderColorsTuple,rgbColDict,numBits))
    userGenderColorsDictRdd.cache()
    if saveOnFile:
        save_training_set_libsvm_format(userGenderColorsDictRdd,numBits)
    return userGenderColorsDictRdd.map(lambda genderDictTuple: map_to_labeled_point(genderDictTuple, numBits))
    
    
def generate_test_set(sc,testData,s,numBits,numColorFields,rgbColDict):
    userGenderColorsRdd=testData.map(lambda row: get_gender_reduced_colors_from_row(row, s,numColorFields,numBits)) 
    userGenderColorsDictRdd=userGenderColorsRdd.map(lambda genderColorsTuple: map_to_gender_dict_tuple(genderColorsTuple,rgbColDict,numBits))
    return userGenderColorsDictRdd.map(lambda genderDictTuple: map_to_labeled_point(genderDictTuple, numBits))

def temp(row,s,numColFields,defaultColorsTuple):
    t=get_gender_colors_from_row(row,s,numColFields)
    print "t is "
    print t
    b=t[1]!=defaultColorsTuple
    return b



def filter_out_default_colors(targetRdd,s,numColFields):
    print "******************************************************************"
    defaultColorsTuple=('C0DEED','333333','0084B4','DDEEF6','C0DEED')
    defaultColorsTuple=defaultColorsTuple[:numColFields]
    return targetRdd.filter(lambda row:get_gender_colors_from_row(row,s,numColFields)[1]!=defaultColorsTuple)
    #return targetRdd.filter(lambda row:temp(row,s,numColFields,defaultColorsTuple))
        
    
############################
##      DATA STATS        ##
############################

def log_color_results(genderColorList,gender,numColsFields):
    logging.info("Stats for gender %s" % gender)
    #print genderColorList
    for colorsGenderTuple in genderColorList:
        logging.info("{0} appear {1} times".format(",".join(colorsGenderTuple[0][1]),colorsGenderTuple[1]))
        
        
def log_color_res_gender(userGenderColorValRdd,gender,numColsFields):
    userGenderColorTotG=userGenderColorValRdd.filter(lambda pairTuple: pairTuple[0][0]==gender and pairTuple[1] > 1).collect()
    userGenderColorTotG=sorted(userGenderColorTotG,key=lambda pairTuple:pairTuple[1],reverse=True)
    userGenderColorTotG=userGenderColorTotG[:10]
    log_color_results(userGenderColorTotG, gender,numColsFields)


def stats_on_data_user_colors_not_reduced(sc,s,currRdd,numColsFields):
    userGenderColorsRdd=currRdd.map(lambda row: get_gender_colors_from_row(row, s,numColsFields))
    
    userGenderColorValRdd=userGenderColorsRdd.map(lambda colorsTuple: ((colorsTuple[0],colorsTuple[1]),1)).reduceByKey(add)
    log_color_res_gender(userGenderColorValRdd, "m",numColsFields)
    log_color_res_gender(userGenderColorValRdd, "f",numColsFields)
    log_color_res_gender(userGenderColorValRdd, "x",numColsFields)    
 
def t(colorsTuple,nbits):
    print "colorsTuple is"
    print colorsTuple
    lst=[((colorsTuple[0],(utils.rgb_to_hex(utils.change_RGB_bit_depth(rgbCol,nbits,24)),)),1) for rgbCol in colorsTuple[1]]
    print "lst is"
    print lst
    return lst    
    
def stats_on_data_user_colors_reduced(sc,s,currRdd,numColsFields,nbits):
    userGenderColorsRdd=currRdd.map(lambda row: get_gender_reduced_colors_from_row(row, s,numColsFields,nbits))
    
    userGenderColorValRdd=userGenderColorsRdd.flatMap(lambda colorsTuple: t(colorsTuple,nbits)).reduceByKey(add)
    log_color_res_gender(userGenderColorValRdd, "m",numColsFields)
    log_color_res_gender(userGenderColorValRdd, "f",numColsFields)
    log_color_res_gender(userGenderColorValRdd, "x",numColsFields)    

def generate_stats_on_data_user_colors(sc,trainingRdd,testRdd,s,numColsFields,nbits):
    '''
    @param s: char separator
    @param twProfilesRdd: rdd containing raw tw_user_profiles
    '''
    
    #logging.info("Total profiles")
    #stats_on_data_user_colors_not_reduced(sc,s,twProfilesRdd,numColsFields)
    logging.info("-------------------------------------------------------")
    logging.info("Training profiles")
    stats_on_data_user_colors_not_reduced(sc,s,trainingRdd,numColsFields)
    logging.info("Training profiles color reduced")
    stats_on_data_user_colors_reduced(sc,s,trainingRdd,numColsFields,nbits)

    logging.info("-------------------------------------------------------")
    logging.info("Test profiles")
    stats_on_data_user_colors_not_reduced(sc,s,testRdd,numColsFields)
    logging.info("test profiles color reduced")
    stats_on_data_user_colors_reduced(sc,s,testRdd,numColsFields,nbits)


def generate_sets_and_evaluate_algorithms_user_colors(sc,s,trainingRdd,testRdd,numBits,numColsFields,saveOnFile=False):
    logging.info( "evaluation with numCols = {0} and num bits = {1}".format(numColsFields,numBits))
    rgbColDict=generate_and_save_rgb_col_dict(numBits,saveOnFile)
    trainingRdd=generate_training_model(sc, trainingRdd, s, numBits, numColsFields,rgbColDict,saveOnFile)
    testRdd=generate_test_set(sc, testRdd, s, numBits, numColsFields,rgbColDict)
    #numVals=pow(2,numBits)
    utils.evaluate_naive_bayes(trainingRdd, testRdd, 1.0)
    utils.evaluate_decision_tree(trainingRdd, testRdd,8,64) #,{ i:(numVals-1) for i in xrange(numColsFields)}
    utils.evaluate_decision_tree(trainingRdd, testRdd,8,64,'entropy') #,{ i:(numVals-1) for i in xrange(numColsFields)}
    utils.evaluate_random_forest(trainingRdd, testRdd)
    utils.evaluate_majority_svm(trainingRdd, testRdd)

def start_analysis(sc, trainingRDDRaw, testRDDRaw, s,filterFunc=None):
    currTrainingRdd=trainingRDDRaw
    currTestRdd=testRDDRaw
    for nColsFields in range(1,6):
        for nBits in range(9,13,3):
            logging.info("")
            logging.info("")
            logging.info("***************************************")
            logging.info("NEW ANALYSIS:")
            logging.info("***************************************")
            if filterFunc:
                currTrainingRdd=filterFunc(trainingRDDRaw,s,nColsFields)
                currTestRdd=filterFunc(testRDDRaw,s,nColsFields)
            generate_stats_on_data_user_colors(sc, currTrainingRdd, currTestRdd, s, nColsFields,nBits)
            generate_sets_and_evaluate_algorithms_user_colors(sc,s,currTrainingRdd,currTestRdd,nBits,nColsFields)
            
           
if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='extract features from tw_profiles')
    
    parser.add_argument('--all', help='one file that will be divided in training and test')
    parser.add_argument('--test', help='input path where training tw_profiles data are stored')
    parser.add_argument('--training', help='input path where training tw_profiles data are stored')
    
    logging.basicConfig(filename='user_color.log',level=logging.INFO)
    
    sc = SparkContext(appName="TwProfilesUserColors") #,pyFiles=pyFls
    
    args = parser.parse_args()
    
    if args.all:
        allProfiles=sc.textFile(args.all)
        (trainingRDDRaw,testRDDRaw)=allProfiles.randomSplit([0.7,0.3])
        print "saving training set.."
        trainingRDDRaw.saveAsTextFile("/tmp/colours_training_set")
        print "saving test set.."
        testRDDRaw.saveAsTextFile("/tmp/colours_test_set")
    elif args.test and args.training:
        trainingRDDRaw=sc.textFile(args.training)
        testRDDRaw=sc.textFile(args.test)
    else:
        raise RuntimeError("you must provide an argument chosing between all or training and test")    
    
    trainingRDDRaw.cache()
    testRDDRaw.cache()
    
    s='\x01'
    
    utils.show_total_stats(trainingRDDRaw, "Training set",s)
    utils.show_total_stats(testRDDRaw, "Test set",s)
    
    logging.info("=========================================")
    logging.info("Try with entire set..")
    start_analysis(sc, trainingRDDRaw, testRDDRaw, s)
    
    logging.info("=========================================")
    logging.info("Try filtering out default colors..")
    start_analysis(sc, trainingRDDRaw, testRDDRaw, s,lambda targetRdd,s,nColsFields: filter_out_default_colors(targetRdd, s, nColsFields))
    