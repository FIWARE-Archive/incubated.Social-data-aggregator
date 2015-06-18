package com.tilab.ca.sda.sda.model;

import java.io.Serializable;
import java.time.ZonedDateTime;


public class TwUserProfile implements Serializable{
    
    private long uid;
    private String name;
    private String screenName;
    private String description;
    private ZonedDateTime lastUpdate;
    
    private String[] profileColors;

    public TwUserProfile() {
    }

    public TwUserProfile(long uid) {
        this.uid = uid;
    }
    
    public TwUserProfile(long uid,String[] profileColors) {
        this.uid = uid;
        this.profileColors=profileColors;
    }
    
    public TwUserProfile(long uid,String description) {
        this.uid = uid;
        this.description=description;
    }
    
    public TwUserProfile(long uid,String name,String screenName,String description,String[] profileColors) {
        this.uid = uid;
        this.description=description;
        this.name=name;
        this.screenName=screenName;
        this.profileColors=profileColors;
    }
    
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

    public ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(ZonedDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    
}
