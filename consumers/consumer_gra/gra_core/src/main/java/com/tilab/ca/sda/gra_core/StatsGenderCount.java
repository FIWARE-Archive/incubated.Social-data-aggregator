package com.tilab.ca.sda.gra_core;

import java.io.Serializable;


public class StatsGenderCount implements Serializable{
    
    private static final int TW_MATRIX_ROWS=4;
    private static final int TW_MATRIX_COLS=3;
    
    private int[][] twMatrix=new int[TW_MATRIX_ROWS][TW_MATRIX_COLS];
    
    private static final int TW_COL_POS=0;
    private static final int RTW_COL_POS=1;
    private static final int RPLY_COL_POS=2;
    
    
  
    public StatsGenderCount(GenderTypes gender,boolean isRetweet,boolean isReply) {
        twMatrix[(int)gender.toLabel()][getPos(isRetweet, isReply)]=1;
    }
    
    private int getPos(boolean isRetweet,boolean isReply){
        if(isRetweet)
            return RTW_COL_POS;
        else if(isReply)
            return RPLY_COL_POS;
        else
            return TW_COL_POS;
    }
    
    
    public StatsGenderCount sum(StatsGenderCount other){
        for(int i=0;i<TW_MATRIX_ROWS;i++)
            for(int t=0;i<TW_MATRIX_COLS;t++)
                twMatrix[i][t]+=other.twMatrix[i][t];
        return this;
    }
    
    public int getNumTwMales() {
        return twMatrix[(int)GenderTypes.MALE.toLabel()][TW_COL_POS];
    }
    
    public int getNumRTwMales() {
        return twMatrix[(int)GenderTypes.MALE.toLabel()][RTW_COL_POS];
    }
    
    public int getNumRplyMales() {
        return twMatrix[(int)GenderTypes.MALE.toLabel()][RPLY_COL_POS];
    }
    
    public int getNumTwFemales() {
        return twMatrix[(int)GenderTypes.FEMALE.toLabel()][TW_COL_POS];
    }
    
    public int getNumRTwFemales() {
        return twMatrix[(int)GenderTypes.FEMALE.toLabel()][RTW_COL_POS];
    }
    
    public int getNumRplyFemales() {
        return twMatrix[(int)GenderTypes.FEMALE.toLabel()][RPLY_COL_POS];
    }
    
    public int getNumTwPages() {
        return twMatrix[(int)GenderTypes.PAGE.toLabel()][TW_COL_POS];
    }
    
    public int getNumRTwPages() {
        return twMatrix[(int)GenderTypes.PAGE.toLabel()][RTW_COL_POS];
    }
    
    public int getNumRplyPages() {
        return twMatrix[(int)GenderTypes.PAGE.toLabel()][RPLY_COL_POS];
    }
    
    public int getNumTwUnknown() {
        return twMatrix[(int)GenderTypes.UNKNOWN.toLabel()][TW_COL_POS];
    }
    
    public int getNumRTwUnknown() {
        return twMatrix[(int)GenderTypes.UNKNOWN.toLabel()][RTW_COL_POS];
    }
    
    public int getNumRplyUnknown() {
        return twMatrix[(int)GenderTypes.UNKNOWN.toLabel()][RPLY_COL_POS];
    }

}
