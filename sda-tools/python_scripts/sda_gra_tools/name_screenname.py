import re
from pyspark import SparkContext
import argparse
import logging
import utils

UNKNOWN="u"
MALE="m"
FEMALE="f"
PAGE="x"

def get_user_name_screenname_from_row(row,s):
    rowParts=row.split(s)
    return (rowParts[1].encode("utf8","ignore"),rowParts[2].encode("utf8","ignore"),rowParts[4]) #name screenName gender

def get_longest_prefix_from_screenname(screenName):
    lenMaxName=0
    longestPrefixName=None
    for name in namesGenderDict.keys():
        if  screenName.startswith(name) or screenName.endswith(name) and len(name) > lenMaxName:
            lenMaxName=len(name)
            longestPrefixName=name
    return namesGenderDict[longestPrefixName] if longestPrefixName else UNKNOWN
               
def clean_name(name):
    name=re.sub("\W+|_|\d", " ", name)
    if not re.match('^([A-Z]+)$', name):
        name=re.sub(r"([A-Z])",r" \1",name)
    name=re.sub("( )+", " ", name)
    return name.lower().strip()

def get_name_gender_dict_from_file(filename):
    """
    the file is structured the following way:
    name,gender 
    where gender can be:
    m -> male
    f -> female
    x -> ambiguous (can be used both for male and for female)
    y -> can be used for female or as a second name for male
    """
    with open(filename,"rb") as fp:
        return {name:gender for name,gender in (line.rstrip().split(",") for line in fp)}

def get_gender_or_default(name,defaultVal):
    if name in namesGenderDict:
        return namesGenderDict[name]
    return defaultVal

def can_be_composite_surname(pos,reconList):
    if pos==0:
        return False
    return reconList[pos-1]==UNKNOWN

def get_gender_from_ambiguous_from_position(reconList):
    firstPostF=reconList.index(FEMALE)
    firstPostM=reconList.index(MALE)
    
    if len(reconList)==2 and reconList.count('y')==0: #there are just one male name and one female e.g. Anna Alessio
        print "there are just one male and one female names"
        print "users are more likely to put their first name before the surname"
        return MALE if firstPostM<firstPostF else FEMALE
        
    if 'y' in reconList: #situation in which there is an ambiguous name related to the context e.g Francesco Maria Furnari
        firstPostY=reconList.index('y')
        if firstPostY-1>0 and reconList[firstPostY-1]==MALE:
            return MALE
        else:
            return FEMALE
    #check if there is a composite surname e.g. Di Paola
    couldBeFemaleSurname=can_be_composite_surname(firstPostF, reconList)
    couldBeMaleSurname=can_be_composite_surname(firstPostM, reconList)
    if (couldBeMaleSurname == couldBeFemaleSurname):
        #users are more likely to put their first name before the surname
        return MALE if firstPostM<firstPostF else FEMALE
    else:
        return MALE if couldBeFemaleSurname else FEMALE
    

def get_gender_from_ambiguous_values(recognList,numM,numF):
    diffMaleFemale=numM-numF
    if diffMaleFemale==0:
        return get_gender_from_ambiguous_from_position(recognList)
    return MALE if diffMaleFemale>0 else FEMALE
        
def get_gender_from_reclist(recognList):
    numF=recognList.count('f')+ recognList.count('y') #consider also names that are females but can be used as second name for males
    numM=recognList.count('m')
    
    if recognList.count(PAGE)>0:
        return PAGE
    elif numM>0 and numF>0:
        print "recognized both m and f values"
        return get_gender_from_ambiguous_values(recognList,numM,numF)
    elif numM+numF==0: 
        return UNKNOWN
    else:
        return MALE if numM>0 else FEMALE
    
def get_gender_from_name(name):
    print "received name {}".format(name)
    cleanName=clean_name(name)
    print "name after clean is {}".format(cleanName)
    nameParts=cleanName.split(" ")
    recognList=[get_gender_or_default(name, 'u') for name in nameParts]
    print "recognList is "
    print recognList
    return get_gender_from_reclist(recognList)

def get_gender_from_screenname(screenName):
    print "get gender from screen name"
    gender=get_gender_from_name(screenName)
    if gender==UNKNOWN:
        print "gender not recognized. Try from longest name match into screenName.."
        gender=get_longest_prefix_from_screenname(screenName)
    print "found gender {}".format(gender)
    return gender

def get_gender_from_name_screenname_tuple(nameScreenNameTuple):
    gender=get_gender_from_name(nameScreenNameTuple[0])
    if gender==UNKNOWN:
        gender=get_gender_from_screenname(nameScreenNameTuple[1])
    print "recognized gender {0} for user with name '{1}' screenName '{2}'".format(gender,nameScreenNameTuple[0],nameScreenNameTuple[1])
    return (gender,nameScreenNameTuple[2])

def get_gender_from_rdd(profileRddRaw,s):
    resultsRdd=profileRddRaw.map(lambda row: get_user_name_screenname_from_row(row, s)) \
                 .map(lambda nameScreenNameTuple: get_gender_from_name_screenname_tuple(nameScreenNameTuple)) \
    
    recognizedRdd=resultsRdd.filter(lambda genderTuple: genderTuple[0]==genderTuple[1])
    accuracyMale=1.0 *recognizedRdd.filter(lambda genderTuple: genderTuple[0]=="m").count()/resultsRdd.filter(lambda genderTuple: genderTuple[1]=="m").count()
    accuracyFemale=1.0 *resultsRdd.filter(lambda genderTuple: genderTuple[0]=="f").count()/resultsRdd.filter(lambda genderTuple: genderTuple[1]=="f").count()
    accuracyPage=1.0 *resultsRdd.filter(lambda genderTuple: genderTuple[0]=="x").count()/resultsRdd.filter(lambda genderTuple: genderTuple[1]=="x").count()
    accuracyTotal=1.0 * recognizedRdd.count() / profileRddRaw.count()
    
    logging.info( "**********************************************************")
    logging.info( "gender from name/screenName accuracy is {0:.2f} %".format(accuracyTotal*100))    
    logging.info( "gender from name/screenName accuracy on male is {0:.2f} %".format(accuracyMale*100)  )  
    logging.info( "gender from name/screenName accuracy on female is {0:.2f} %".format(accuracyFemale*100) )   
    logging.info( "gender from name/screenName accuracy on pages is {0:.2f} %".format(accuracyPage*100)  )  
                       

def tuple_with_gender_from_row(row,s):
    resList=row.split(s)
    print resList
    nameScreenName=(resList[1].encode("utf8","ignore"),resList[2].encode("utf8","ignore"),resList[4])
    gender=get_gender_from_name_screenname_tuple(nameScreenName)
    resList[4]=gender[0]
    return tuple(resList) 

def save_gender_from_raw_rdd(profileRddRaw,s):
    profileRddRaw.map(lambda row: tuple_with_gender_from_row(row, s)) \
                 .filter(lambda tp: tp[4]!=UNKNOWN and tp[9]!="-") \
                 .map(lambda tp: s.join(tp)) \
                 .saveAsTextFile("train_colors")


namesGenderDict=get_name_gender_dict_from_file("names_gender.txt")


if __name__ == '__main__':
    s='\x01'
    parser = argparse.ArgumentParser(description='extract features from tw_profiles')
    
    parser.add_argument('--test', help='input path where test tw_profiles data are stored')
    
    logging.basicConfig(filename='user_name_screenname.log',level=logging.INFO)
    
    sc = SparkContext(appName="TwProfilesNameScreenName",pyFiles=utils.modules_to_load(['utils.py'])) 
    args = parser.parse_args()
    
    profilesRDDRaw=sc.textFile(args.test)
    
    logging.info("Try with the whole test set..")
    get_gender_from_rdd(profilesRDDRaw, s)
    
    logging.info("Try with test set of only names clearly written..")
    namesRddRaw=profilesRDDRaw.filter(lambda row: int(row.split(s)[6])==1)
    get_gender_from_rdd(namesRddRaw, s)
    
