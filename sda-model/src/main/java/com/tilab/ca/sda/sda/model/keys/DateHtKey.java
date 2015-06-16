package com.tilab.ca.sda.sda.model.keys;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;


public class DateHtKey implements Serializable {
    private Date date;
    private String ht;
    
    public DateHtKey(Date date, String ht) {
        this.date = date;
        this.ht = ht;
        
    }

    public Date getDate() {
        return date;
    }

    public String getHt() {
        return ht;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setHt(String ht) {
        this.ht = ht;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.date);
        hash = 97 * hash + Objects.hashCode(this.ht);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DateHtKey other = (DateHtKey) obj;
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.ht, other.ht)) {
            return false;
        }
        return true;
    }
    
}