package com.tilab.ca.sda.gra_core;

import java.io.Serializable;
import org.apache.spark.api.java.JavaRDD;


public class DescrResults implements Serializable{
    
    private  JavaRDD<ProfileGender> profilesRecognized;
    private  JavaRDD<ProfileGender> profilesUnrecognized;
    

    public DescrResults(JavaRDD<ProfileGender> profilesRecognized, JavaRDD<ProfileGender> profilesUnrecognized) {
        this.profilesRecognized = profilesRecognized;
        this.profilesUnrecognized = profilesUnrecognized;
    }

    public JavaRDD<ProfileGender> getProfilesRecognized() {
        return profilesRecognized;
    }

    public void setProfilesRecognized(JavaRDD<ProfileGender> profilesRecognized) {
        this.profilesRecognized = profilesRecognized;
    }

    public JavaRDD<ProfileGender> getProfilesUnrecognized() {
        return profilesUnrecognized;
    }

    public void setProfilesUnrecognized(JavaRDD<ProfileGender> profilesUnrecognized) {
        this.profilesUnrecognized = profilesUnrecognized;
    }
}
