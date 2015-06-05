package com.tilab.ca.sda.gra_core;

public enum GenderTypes {
    MALE('m',0.0),
    FEMALE('f',1.0),
    PAGE('x',2.0),
    DEPENDANT('y',3.0), //can be used for females or as a second name for males
    UNKNOWN('u',4.0);
    
    private char genderChar;
    private double label;
    
    private GenderTypes(char c,double lb){
        genderChar=c;
        label=lb;
    }
    
    public char toChar(){
        return genderChar;
    }
    
    public double toLabel(){
        return label;
    }
    
    
    public static GenderTypes fromChar(char c){
        switch(c){
            case 'm': return MALE;
            case 'f': return FEMALE;
            case 'x': return PAGE;
            case 'y': return DEPENDANT;
            default: return UNKNOWN;
        }
    }
    
    public static GenderTypes fromLabel(double doubleLabel){
        int intLabel=(int)doubleLabel;
        switch(intLabel){
            case 0: return MALE;
            case 1: return FEMALE;
            case 2: return PAGE;
            default: return UNKNOWN;
        }
    }
}
