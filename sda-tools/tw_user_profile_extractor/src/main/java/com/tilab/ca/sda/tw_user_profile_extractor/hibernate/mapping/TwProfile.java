package com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping;

import java.io.Serializable;
import java.lang.reflect.Field;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.apache.log4j.Logger;

@MappedSuperclass
public class TwProfile implements Serializable{
    
    private static final Logger log=Logger.getLogger(TwProfile.class);
    
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
    
    protected String url;
    
    @Column(name="profile_link_color")
    protected String profileLinkColor;
    
    @Column(name="profiles_sidebar_fill_color")
    protected String profileSidebarFillColor;
    
    @Column(name="profiles_sidebar_border_color")
    protected String profileSidebarBorderColor;        
    
    
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProfileLinkColor() {
        return profileLinkColor;
    }

    public void setProfileLinkColor(String profileLinkColor) {
        this.profileLinkColor = profileLinkColor;
    }

    public String getProfileSidebarFillColor() {
        return profileSidebarFillColor;
    }

    public void setProfileSidebarFillColor(String profileSidebarFillColor) {
        this.profileSidebarFillColor = profileSidebarFillColor;
    }

    public String getProfileSidebarBorderColor() {
        return profileSidebarBorderColor;
    }

    public void setProfileSidebarBorderColor(String profileSidebarBorderColor) {
        this.profileSidebarBorderColor = profileSidebarBorderColor;
    }
    
    
    
    @Override
    public String toString(){
        Field[] fields=this.getClass().getFields();
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<fields.length;i++){
            try {
                sb.append(fields[i].get(this).toString());
                sb.append('\001');
            } catch (Exception ex) {
                log.error(ex);
            } 
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

}
