import argparse
from pyspark import SparkContext
import itertools

numBitsIn=24
genderDict={'m':0.0,'f':1.0,'x':2.0}
SOH='\x01'
defaultColorsTuple=('C0DEED','333333','0084B4','DDEEF6','C0DEED')

"""
Expected file format 

gender,profileBackgroundColor,profileTextColor,profileLinkColor,profileSidebarFillColor,profileSidebarBorderColor

Where "," is the fields separator chosen as example 
"""


def get_gender_reduced_colors_from_row(row,fs,numColFields,numBitsOut):
    """
    @param row: raw string containing the data
    @param  fs: field separator
    @param numColFields: number of color fields to take   
    @return a tuple containing (userGender, (profile colours reduced rgb)) 
    where profile colours are:
     - background
     - text
     - link
     - sidebar fill
     - sidebar border
    """
    gcfrTuple=get_gender_colors_from_row(row,fs,numColFields)
    return (gcfrTuple[0],get_RGB_tuple_depth_reduced_from_hex(gcfrTuple[1],numBitsOut))

def get_gender_colors_from_row(row,fs,numColFields):
    """
    @param row: raw string containing the data
    @param  fs: field separator
    @param numColFields: number of colors fields to take   
    @return a tuple containing (userGender, (profile colors hex)) 
    where profile colours are:
     - background
     - text
     - link
     - sidebar fill
     - sidebar border
    """
    fields=row.split(fs)
    return (fields[0],tuple(fields[2:(2+numColFields)])) #fields[0] = gender

def map_to_gender_dict_tuple(genderColorsTuple,rgbColDict):
    """
    Map colors tuple to a dictionary containing id_color:frequency
    @param genderColorsTuple: tuple containing rgb colors scaled to numBits
    @param rgbColDict: dictionary color:color_id
    """
    genderLabel=genderDict[genderColorsTuple[0]]
    currUserColorsDict={}
    for rgbCol in genderColorsTuple[1]:
        if rgbColDict[rgbCol] not in currUserColorsDict:
            currUserColorsDict[rgbColDict[rgbCol]]=0
        currUserColorsDict[rgbColDict[rgbCol]]+=1
    return (genderLabel,currUserColorsDict)

def get_RGB_tuple_depth_reduced_from_hex(hexTuple,numBitsOut):
    """
    Take a tuple of colors hex values as input and return a tuple of 
    RGB colors scaled from 24 bits to the number of bits passed as parameter
    @param hexTuple: Tuple containing hex colors 
    @param numBitsOut: number of bits to scale colors 
    @return: a tuple containing the scaled colors 
    """
    rgbTuples=tuple(hex_to_rgb(val) for val in hexTuple)
    return tuple(change_RGB_bit_depth(rgbTuple, numBitsIn, numBitsOut) for rgbTuple in rgbTuples)

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

def generate_palette_RGB(numBits):
    """
    generate all the combinations for rgb with the number 
    of bits passed as parameter
    @param numBits: number of bits for each channel
    @return a list of tuples containing all the possible combinations
    """
    print "there are %d possible combination with %d bits" %(pow(pow(2, numBits),3),numBits)
    return [combination for combination in itertools.product(xrange(pow(2, numBits)), repeat=3)] 

def map_to_libsvm_format(genderDictTuple):
    spVectorList=["{0}:{1}".format((key+1),genderDictTuple[1][key]) for key in sorted(genderDictTuple[1].keys())]
    #print "spVectorList is"
    #print spVectorList
    return "{0} {1}".format(int(genderDictTuple[0])," ".join(spVectorList))

def save_training_set_libsvm_format(userGenderColorsDictRdd,numBits):
    userGenderColorsDictRdd.map(lambda genderColorsDictTuple: map_to_libsvm_format(genderColorsDictTuple)) \
                           .saveAsTextFile("gen/training_colour_libsvm_data_{0}".format(numBits))


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='generate user profile colors training set libsvm format for GRA application')
    
    parser.add_argument('--i', help='input file in the format gender,profileBackgroundColor,profileTextColor,profileLinkColor,profileSidebarFillColor,profileSidebarBorderColor')
    parser.add_argument('--fs', default=SOH, help='file fields separator (default SOH)')
    parser.add_argument('--numcols', default=4, help='number of colors to consider (default 4 over 5)')
    parser.add_argument('--nbits', default=9, help='number of bits for RGB color scaling (default 9)')
    parser.add_argument('--fdc', action='store_true', default=False, help='filter default colors from training set')
    
    
    args = parser.parse_args()
    
    print "Creating spark context.."
    sc = SparkContext(appName="TwProfilesUserColors") 
    
    print "loading data from {0}".format(args.i)
    trainingRDDRaw=sc.textFile(args.i)
    
    numCols=int(args.numcols)
    numBitsOut=int(args.nbits)
    
    print "chosen num bits={0} and num colors = {1}".format(numBitsOut,numCols)
                                                            
    trainingColorsRdd=trainingRDDRaw.map(lambda row: get_gender_colors_from_row(row,args.fs,numCols))
    
    if args.fdc:
        print "filtering out profiles that contains default colors from training set.."
        defaultColorsTuple=defaultColorsTuple[:numCols]
        trainingColorsRdd=trainingColorsRdd.filter(lambda genderHexColorsTuple:genderHexColorsTuple[1]!=defaultColorsTuple)
    
    trainingRGBScaledColorsRdd=trainingColorsRdd.map(lambda genderHexColorsTuple:(genderHexColorsTuple[0],
                                                                                  get_RGB_tuple_depth_reduced_from_hex(genderHexColorsTuple[1],
                                                                                                                       numBitsOut)))
    rgbColDict={val:index for index,val in enumerate(generate_palette_RGB(numBitsOut/3))}
    
    print "mapping colors to indexes.."
    userGenderColorsRdd=trainingRGBScaledColorsRdd.map(lambda genderColorsTuple: map_to_gender_dict_tuple(genderColorsTuple,rgbColDict)) 
                                                
    print "saving data in libsvm format.."
    save_training_set_libsvm_format(userGenderColorsRdd, numBitsOut)

    print "gra_usr_color END"