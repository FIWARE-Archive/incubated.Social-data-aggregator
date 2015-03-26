package com.tilab.ca.sda.tw_user_profile_extractor;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="tw_profiles_raw")
public class TwProfile implements Serializable{
    
    public static final String TRAINING_TYPE="TRAINING";
    public static final String TEST_TYPE="TEST";
    
    @Column(name="user_id")
    private long userId;
    
    private String name;
    
    @Column(name="screen_name")
    private String screenName;
    
    private String description;
    
    @Column(name="profile_background_color")
    private String profileBackgroundColor;
    
    @Column(name="profile_text_color")
    private String profileTextColor;
    
    private char gender;
    
    @Column(name="is_person")
    private boolean isPerson;
    
    @Column(name="data_type")
    private String dataType;

    
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public String getProfileBackgroundColor() {
        return profileBackgroundColor;
    }

    public void setProfileBackgroundColor(String profileBackgroundColor) {
        this.profileBackgroundColor = profileBackgroundColor;
    }

    public String getProfileTextColor() {
        return profileTextColor;
    }

    public void setProfileTextColor(String profileTextColor) {
        this.profileTextColor = profileTextColor;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public boolean isIsPerson() {
        return isPerson;
    }

    public void setIsPerson(boolean isPerson) {
        this.isPerson = isPerson;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    @Override
    public String toString(){
        return String.format("%d\001%s\001%s\001%s\001%s\001%s\001%c\001%b\001%s",userId,name,screenName,description,profileBackgroundColor,
                                                                                  profileTextColor,gender,isPerson,dataType);
    }
    
}
