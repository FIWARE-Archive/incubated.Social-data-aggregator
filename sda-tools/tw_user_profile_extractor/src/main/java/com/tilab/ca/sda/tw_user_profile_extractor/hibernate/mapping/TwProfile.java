package com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class TwProfile implements Serializable{
    
    @Id
    @Column(name="user_id",updatable=false)
    protected long userId;
    
    protected String name;
    
    @Column(name="screen_name")
    protected String screenName;
    
    protected String description;
    
    @Column(name="profile_background_color")
    protected String profileBackgroundColor;
    
    @Column(name="profile_text_color")
    protected String profileTextColor;
    
    @Column(name="location")
    protected String location;
    
    @Column(name="gender",updatable=false)
    protected char gender;
    
    protected transient long postId;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }
    
    @Override
    public String toString(){
        return String.format("%d\001%s\001%s\001%s\001%s\001%s\001%c",userId,name,screenName,description,profileBackgroundColor,
                                                                                  profileTextColor,gender);
    }
    
}
