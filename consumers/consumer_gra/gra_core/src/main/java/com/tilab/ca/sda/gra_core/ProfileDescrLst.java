package com.tilab.ca.sda.gra_core;

import com.tilab.ca.sda.sda.model.TwUserProfile;
import java.io.Serializable;
import java.util.List;


public class ProfileDescrLst implements Serializable{
    
    private TwUserProfile profile;
    private List<String> descrStrLst;

    public ProfileDescrLst(TwUserProfile profile, List<String> descrStrLst) {
        this.profile = profile;
        this.descrStrLst = descrStrLst;
    }

    public TwUserProfile getProfile() {
        return profile;
    }

    public void setProfile(TwUserProfile profile) {
        this.profile = profile;
    }

    public List<String> getDescrStrLst() {
        return descrStrLst;
    }

    public void setDescrStrLst(List<String> descrStrLst) {
        this.descrStrLst = descrStrLst;
    }
    
    
}
