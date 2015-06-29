import argparse
from pyspark import SparkContext
import logging
import re
from pyspark.mllib.feature import HashingTF,IDF
from pyspark.mllib.linalg import Vectors
import ast
from user_descr import log2numFeatures
from gi.overrides.keysyms import union

genderDict={'m':0.0,'f':1.0,'x':2.0}
SOH='\x01'

"""
Expected file format 

gender,description

Where "," is the fields separator chosen as example 
"""

def get_gender_description_from_row(row,s,smilesList,stopWordsList):
    """
    Return a tuple in the format (gender,[list of cleaned words from description]
    @param row: the row in the format gender,description
    @param s: field separator
    @param smilesList: list of smiles that can be contained into the description
    @param stopWordsList: a list of stopwords that don't have to be considered during the generation
                          of words list  
    """
    fields=row.split(s)
    print fields
    return (fields[0],get_word_keys_list(smilesList, stopWordsList, fields[1]))

def get_list_from_file(filename):
    """
    Return a list containing a file row for each element
    """
    with open(filename,"rb") as sFp:
        return sFp.read().splitlines()

def get_smiles_list():
    return get_list_from_file("smiles.txt")
    
def get_stop_words_list():
    return get_list_from_file("stopWords.txt")

def clear_description(descriptionText):
    """
    clear the description from mentions,numbers,CR,LF,web pages references and 
    transform hashtags to the corresponding word  
    """
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

def clean_word(smileList,word):
    """
    clear everything is not an alphabetic character
    """
    if word not in smileList:
        word=re.sub("[^\\w]+", " ", word)   
    return word.strip()

def is_ok_as_key(stopWordsList,word):
    """
    check if the provided key is valid.
    @return: True if it is a valid key, False otherwise
    """
    return word and len(word) >=3 and word not in stopWordsList  

def get_word_keys_list(smilesList,stopWordsList,description):
    """
    generate a list of keywords from user description
    """
    words=clear_description(description).split(" ")
    return [clean_word(smilesList, word) for word in words if is_ok_as_key(stopWordsList, word)]

def generate_gender_tf(twProfilesRdd,numFe):
    """
    Generate Term Frequency tuple (gender,sparse vector) from rdd containing following tuples:
    (gender,(clean words tuple))
    """
    tf = HashingTF(numFeatures = numFe)
    return twProfilesRdd.map(lambda genderDescrTuple: (genderDict[genderDescrTuple[0]],tf.transform(genderDescrTuple[1])))

def generate_tf_idf(twProfilesRdd,numFe):
    """
    Generate TF IDF tuple (gender,sparse vector) from rdd containing following tuples:
    (gender,(clean words tuple))
    """
    gtlp=generate_gender_tf(twProfilesRdd, numFe)
    idf=IDF()
    tfVectorsRDD=gtlp.map(lambda tp: tp[1])
    idfModel=idf.fit(tfVectorsRDD)
    idfRdd=idfModel.transform(tfVectorsRDD)
    return (idfRdd.zip(gtlp).map(lambda tp:(tp[1][0],tp[0])),idfModel)

def tf_IDF_switch(trainingRDDGenderDescr,numFeatures,algo):
    """
      Generate training Rdd (gender,sparseVector). The algorithm to generate the sparse 
      vector can be either tf or tf-idf
    """
    if algo=="tf":
        trainingRdd=generate_gender_tf(trainingRDDGenderDescr, numFeatures)
    elif algo=="tf-idf":
        trainingRdd,idfModel=generate_tf_idf(trainingRDDGenderDescr, numFeatures)
    else:
        raise RuntimeError("algo can be one of 'tf' or 'tf-idf'. Unrecognized "+algo)    
    
    return trainingRdd

def map_to_libsvm_format_descr(tp):
    """
    map the training rdd to libsvm format (label index:value index:value...)
    """
    spVectorList=[]
    vectStr=Vectors.stringify(tp[1])[1:]
    (dim,indexStr,valsStr)=vectStr[:-1].split("[")
    indexLst=ast.literal_eval("["+indexStr)[0]
    valsLst=ast.literal_eval("["+valsStr)
    print "sparse vector-------------------------------"
    print tp[1]
    print "indexes-------------------------------"
    print indexLst
    print "vals-------------------------------"
    print valsLst
    for i in xrange(len(indexLst)):
        print i
        spVectorList.append("{0}:{1}".format((int(indexLst[i])+1),int(valsLst[i]))) #for this format indexes start from 1
    print "spVectorList is"
    print spVectorList
    return "{0} {1}".format(int(tp[0])," ".join(spVectorList))

def save_training_set_libsvm_format_descr(lpDescrRdd):
    """
    Save the training set libsvm format on file
    """
    #insert a fake row needed because otherwise the dense vector size will be the maximum index found for the training set
    #this can lead to issues during the execution phase of gra core
    fakeRowRdd=sc.parallelize(["{0} {1}:{2}".format(0,pow(2,log2numFeatures),0)])
    lpDescrRdd.map(lambda tp: map_to_libsvm_format_descr(tp)).union(fakeRowRdd).saveAsTextFile("gen/training_descr_libsvm_data")



if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='generate user profile description training set libsvm format for GRA application')
    
    parser.add_argument('--i', help='input file in the format gender,description')
    parser.add_argument('--fs', default=SOH, help='file fields separator (default SOH)')
    parser.add_argument('--nf', default=18, help='log2 num features (expressed as a power of 2)(DEFAULT 18)')
    parser.add_argument('--algo', default="tf-idf", help='algorithm used to generate sparse vector (tf,tf-idf) (default tf-idf)')
    
    logging.basicConfig(filename='gra_user_descr.log',level=logging.INFO)
    
    sc = SparkContext(appName="TwProfilesUserDescr") #,pyFiles=pyFls
    
    args = parser.parse_args()
    
    trainingRDDRaw=sc.textFile(args.i)
    
    log2numFeatures=int(args.nf)
    
    smilesList=get_smiles_list()
    stopWordsList=get_stop_words_list()
    fs=args.fs
    
    trainingRDDGenderDescr=trainingRDDRaw.map(lambda row: get_gender_description_from_row(row, fs,smilesList,stopWordsList))
    
    trainingRDD=tf_IDF_switch(trainingRDDGenderDescr, pow(2,log2numFeatures), args.algo)
    
    save_training_set_libsvm_format_descr(trainingRDD)
    