package com.tilab.ca.sda.gra_consumer_dao.data;


public class TwGenderProfile {
    private long uid;
    
    private String screenName;
    
    private char gender;
    
    public TwGenderProfile(){}

    public TwGenderProfile(long uid, String screenName, char gender) {
        this.uid = uid;
        this.screenName = screenName;
        this.gender = gender;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

}
