package com.tilab.ca.sda.gra_core.components;

import com.tilab.ca.sda.gra_core.GenderTypes;
import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.sda.model.TwUserProfile;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


public class GenderNameSN implements Serializable{
    
    private static final Logger log = Logger.getLogger(GenderNameSN.class);
    
    private static final String ONLY_ALPHABET_LETTERS="\\W+|_|\\d";
    
    private NamesGenderMap namesGenderMap=null;
    
    public GenderNameSN(NamesGenderMap namesGenderMap){
        this.namesGenderMap=namesGenderMap;
    }
    
    
    public GenderTypes getGenderFromNameScreenName(String name,String screenName){
        GenderTypes gender=getGenderFromName(name);
        if(gender==GenderTypes.UNKNOWN)
            gender=getGenderFromScreenName(screenName);
        return gender;
    }
    
    public JavaRDD<ProfileGender> getNamesGenderRDD(JavaRDD<TwUserProfile> twProfilesRdd){
        return twProfilesRdd.map(twProfile -> 
                new ProfileGender(twProfile,getGenderFromNameScreenName(twProfile.getName(), twProfile.getScreenName())));
    }
    
    
    public GenderTypes getGenderFromName(String name){
        log.debug("received name "+name);
        String cleanName=cleanName(name);
        log.debug("name after clean is "+cleanName);
        String[] nameParts=cleanName.split(" ");
        log.debug("generating recognition list..");
        List<GenderTypes> recognList=Arrays.asList(nameParts).stream()
                                                         .map((currName) -> namesGenderMap.getGender(currName))
                                                         .collect(Collectors.toList());
        log.debug(String.format("Recognition list is [%s]",StringUtils.join(recognList,",")));
        return getGenderFromRecognList(recognList);
    }
    
    
    public GenderTypes getGenderFromScreenName(String screenName){
        log.debug("getting gender from screenName "+screenName);
        GenderTypes gt=getGenderFromName(screenName);
        if(gt==GenderTypes.UNKNOWN)
            gt=namesGenderMap.getGenderLongestPrefixName(screenName);
        
        log.debug("from screenName found gender "+gt.toChar());
        return gt;
    }
    
    
    
    private GenderTypes getGenderFromRecognList(List<GenderTypes> recognList){
        int numM=(int)recognList.stream().filter((gender)-> gender==GenderTypes.MALE).count();
        int numF=(int)recognList.stream().filter((gender)-> gender==GenderTypes.FEMALE || gender==GenderTypes.DEPENDANT).count(); //consider also names that are females but can be used as second name for males
        int numP=(int)recognList.stream().filter((gender)-> gender==GenderTypes.PAGE).count();
        
        if(numP>0)
            return GenderTypes.PAGE;
        else if(numM >0 && numF>0)
            return getGenderFromAmbiguousValues(recognList, numM, numF);
        else if(numM+numF==0) 
            return GenderTypes.UNKNOWN;
        else
            return numM>0?GenderTypes.MALE:GenderTypes.FEMALE;
        
    }
    
    private GenderTypes getGenderFromAmbiguousValues(List<GenderTypes> recognList,int numM,int numF){
        int diffMaleFemale=numM-numF;
        if(diffMaleFemale==0)
            return getGenderAmbiguousFromPosition(recognList);
        return diffMaleFemale>0?GenderTypes.MALE:GenderTypes.FEMALE;
    }
    
    private GenderTypes getGenderAmbiguousFromPosition(List<GenderTypes> recognList) {
        int firstPostM = recognList.indexOf(GenderTypes.MALE);
        int firstPostF = recognList.indexOf(GenderTypes.FEMALE);
        int numDependant = (int) recognList.stream().filter((gender) -> gender == GenderTypes.DEPENDANT).count();

        if (recognList.size() == 2 && numDependant == 0) {// #there are just one male name and one female e.g. Anna Alessio
            log.debug("there are just one male and one female names");
            log.debug("users are more likely to put their first name before the surname..guessing from position..");
            return firstPostM < firstPostF ? GenderTypes.MALE : GenderTypes.FEMALE;
        }
        
        if(numDependant>0){ //situation in which there is an ambiguous name related to the context e.g Francesco Maria Furnari
            int firstPostDep=recognList.indexOf(GenderTypes.DEPENDANT);
            return firstPostDep>0 && recognList.get(firstPostDep-1)==GenderTypes.MALE?GenderTypes.MALE:GenderTypes.FEMALE;
        }    
        //check if there is a composite surname e.g. Di Paola
        boolean couldBeFemaleSurname = canBeCompositeSurname(firstPostF, recognList);
        boolean couldBeMaleSurname = canBeCompositeSurname(firstPostM, recognList);
        if(couldBeMaleSurname == couldBeFemaleSurname)
            return firstPostM<firstPostF?GenderTypes.MALE:GenderTypes.FEMALE; //users are more likely to put their first name before the surname
        else
            return couldBeFemaleSurname?GenderTypes.MALE:GenderTypes.FEMALE;
    }
    
    private boolean canBeCompositeSurname(int pos,List<GenderTypes> recognList){
        if(pos==0)
            return false;
        return recognList.get(pos-1)==GenderTypes.UNKNOWN; 
    }
    
    
    public static String cleanName(String name){
        name=name.replaceAll(ONLY_ALPHABET_LETTERS," ");
        if(!name.toUpperCase().equals(name)){
            name=name.replaceAll("([A-Z])"," $1");
        }    
        return name.replaceAll("( )+"," ").toLowerCase().trim();
    }
    
}

