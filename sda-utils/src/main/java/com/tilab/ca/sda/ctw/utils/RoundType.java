package com.tilab.ca.sda.ctw.utils;


public class RoundType {
    public static final int ROUND_TYPE_MIN=0;
    public static final int ROUND_TYPE_HOUR=1;
    public static final int ROUND_TYPE_DAY=2;
    
    
    public static int fromString(String roundTypeString){
        switch(roundTypeString.toLowerCase()){
             case "min": return ROUND_TYPE_MIN;
             case "hour": return ROUND_TYPE_HOUR;
             case "day": return ROUND_TYPE_DAY;
             default:
                 throw new IllegalArgumentException("roundType not recognized");
        }
    }
}
