package com.tilab.ca.sda.sda.model;

import java.io.Serializable;


public class TwUserProfile implements Serializable{
    
    private long uid;
    private String name;
    private String screenName;
    private String description;
    
    private String[] profileColors;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getProfileColors() {
        return profileColors;
    }

    public void setProfileColors(String[] profileColors) {
        this.profileColors = profileColors;
    }
}
