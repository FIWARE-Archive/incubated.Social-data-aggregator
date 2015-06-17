package com.tilab.ca.sda.gra_core;

import java.io.Serializable;


public class StatsGenderCount implements Serializable{
    
    private int numTwMales = 0;
    private int numTwFemales = 0;
    private int numTwPages = 0;
    private int numTwUndefined = 0;
    

    public StatsGenderCount(GenderTypes gender) {
        switch(gender){
            case MALE: numTwMales=1;
                break;
            case FEMALE: numTwFemales=1;
                break;
            case PAGE: numTwPages=1;
                break;
            default: numTwUndefined=1;
        }
    }
    
    public StatsGenderCount(int numMales,int numFemales,int numPages,int numUndefined) {
        this.numTwFemales=numFemales;
        this.numTwMales=numMales;
        this.numTwPages=numPages;
        this.numTwUndefined=numUndefined;
    }
    
    public StatsGenderCount sum(StatsGenderCount other){
        this.numTwFemales+=other.numTwFemales;
        this.numTwMales+=other.numTwMales;
        this.numTwPages+=other.numTwPages;
        this.numTwUndefined+=other.numTwUndefined;
        return this;
    }

    public int getNumTwMales() {
        return numTwMales;
    }

    public void setNumTwMales(int numTwMales) {
        this.numTwMales = numTwMales;
    }

    public int getNumTwFemales() {
        return numTwFemales;
    }

    public void setNumTwFemales(int numTwFemales) {
        this.numTwFemales = numTwFemales;
    }

    public int getNumTwPages() {
        return numTwPages;
    }

    public void setNumTwPages(int numTwPages) {
        this.numTwPages = numTwPages;
    }

    public int getNumTwUndefined() {
        return numTwUndefined;
    }

    public void setNumTwUndefined(int numTwUndefined) {
        this.numTwUndefined = numTwUndefined;
    }

}
