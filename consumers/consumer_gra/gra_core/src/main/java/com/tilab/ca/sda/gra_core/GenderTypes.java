package com.tilab.ca.sda.gra_core;


public enum GenderTypes{
    MALE('m',0.0),
    FEMALE('f',1.0),
    PAGE('x',2.0),
    UNKNOWN('u',3.0),
    DEPENDANT('y',4.0), //can be used for females or as a second name for males
    AMBIGUOUS('a',5.0);
    
    
    private final char genderChar;
    private final double label;
    
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
            case 'a': return AMBIGUOUS; 
            default: return UNKNOWN;
        }
    }
    
    public static GenderTypes fromLabel(double doubleLabel){
        int intLabel=(int)doubleLabel;
        switch(intLabel){
            case 0: return MALE;
            case 1: return FEMALE;
            case 2: return PAGE;
            case 4: return DEPENDANT;
            case 5: return AMBIGUOUS;
            default: return UNKNOWN;
        }
    }
}
