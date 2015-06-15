
package com.tilab.ca.sda.gra_core;

import com.tilab.ca.sda.sda.model.TwUserProfile;
import java.io.Serializable;


public class ProfileGender implements Serializable{
    
    private TwUserProfile twProfile;
    private GenderTypes gender;

    public ProfileGender(TwUserProfile twProfile, GenderTypes gender) {
        this.twProfile = twProfile;
        this.gender = gender;
    }

    public TwUserProfile getTwProfile() {
        return twProfile;
    }

    public void setTwProfile(TwUserProfile twProfile) {
        this.twProfile = twProfile;
    }

    public GenderTypes getGender() {
        return gender;
    }

    public void setGender(GenderTypes gender) {
        this.gender = gender;
    }
}
