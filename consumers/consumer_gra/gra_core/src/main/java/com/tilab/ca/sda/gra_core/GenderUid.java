package com.tilab.ca.sda.gra_core;

import java.io.Serializable;


public class GenderUid implements Serializable{
    
    private long uid;
    private GenderTypes gender;

    public GenderUid(long uid, GenderTypes gender) {
        this.uid = uid;
        this.gender = gender;
    }
    
    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public GenderTypes getGender() {
        return gender;
    }

    public void setGender(GenderTypes gender) {
        this.gender = gender;
    }

}
