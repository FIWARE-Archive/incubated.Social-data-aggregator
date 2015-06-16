package com.tilab.ca.sda.gra_core.components;

import com.tilab.ca.sda.gra_core.GenderTypes;
import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.gra_core.ml.MlModel;
import com.tilab.ca.sda.gra_core.utils.ColourUtils;
import com.tilab.ca.sda.gra_core.utils.GraConstants;
import com.tilab.ca.sda.sda.model.TwUserProfile;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;


public class GenderUserColors implements Serializable{
    
    private static final int numBitsIn=24;
    
    private final MlModel model;
    private final int numBits;
    private final int numColors;
    private final Map<Color,Integer> coloursIndexMap;
    
    
    public GenderUserColors(int numBits,int numColors,MlModel cmodel,JavaSparkContext sc,String trainingPath){
        model=cmodel;
        model.init(sc, trainingPath+GraConstants.COLOUR_TAG+GraConstants.TRAINING_FILE_NAME);
        this.numBits=numBits;
        this.numColors=numColors;
        coloursIndexMap=ColourUtils.generatePaletteRGB(numBits);
    }
    
    
    public JavaRDD<ProfileGender> getGendersFromTwProfiles(JavaRDD<ProfileGender> profilesRDD){
        return profilesRDD.map(twProfileGender -> new ProfileGender(twProfileGender.getTwProfile(), 
                                                                    getGenderFromProfileColours(twProfileGender.getTwProfile())));
    }
    
    
    public GenderTypes getGenderFromProfileColours(TwUserProfile twUserProfile){
        //a list containing the index of each scaled color in coloursIndexMap
        List<Integer> indexLst=Arrays.asList(twUserProfile.getProfileColors())
               .subList(0, numColors)
               .stream()
               .map((hexStrColour) -> ColourUtils.changeRGBbitDepth(ColourUtils.hex2Rgb(hexStrColour), numBitsIn, numBits))
               .map(coloursIndexMap::get)
               .collect(Collectors.toList());
       
       return GenderTypes.fromLabel(model.predict(getSparseVectorFromIndexesLst(indexLst)));
    }
    
    private Vector getSparseVectorFromIndexesLst(List<Integer> indexLst){
        Map<Integer,Integer> mInd=new HashMap<>();
        
        for(Integer elem:indexLst){
            if(!mInd.containsKey(elem))
                mInd.put(elem, 0);
            mInd.put(elem, mInd.get(elem)+1);
        }  
        int sparseVectorSize=mInd.size();
        int[] indexes=new int[sparseVectorSize];
        double[] occurrencies=new double[sparseVectorSize];
        List<Integer> idxLst=new ArrayList(mInd.keySet());
        Collections.sort(idxLst);
        int index=0;
        for(Integer keyIdx:idxLst){
            indexes[index]=keyIdx;
            occurrencies[index]=(double)mInd.get(keyIdx);
            index++;
        }
        return Vectors.sparse(sparseVectorSize,indexes,occurrencies);
    }
  
}
