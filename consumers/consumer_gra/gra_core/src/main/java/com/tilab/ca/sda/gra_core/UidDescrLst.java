package com.tilab.ca.sda.gra_core;

import java.util.List;


public class UidDescrLst {
    
    private long uid;
    private List<String> descrStrLst;

    public UidDescrLst(long uid, List<String> descrStrLst) {
        this.uid = uid;
        this.descrStrLst = descrStrLst;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public List<String> getDescrStrLst() {
        return descrStrLst;
    }

    public void setDescrStrLst(List<String> descrStrLst) {
        this.descrStrLst = descrStrLst;
    }
    
    
}
