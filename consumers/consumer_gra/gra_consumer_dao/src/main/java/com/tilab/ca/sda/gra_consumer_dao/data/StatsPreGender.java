package com.tilab.ca.sda.gra_consumer_dao.data;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass()
public class StatsPreGender implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    protected int id;
    
    @Column(name = "aggr_time")
    protected Date createdAt = null;
    
    protected int gran;
    
    @Column(name = "num_tw_males")
    protected int numTWMales;
    
    @Column(name = "num_rtw_males")
    protected int numRTWMales;
    
    @Column(name = "num_reply_males")
    protected int numRplyMales;
    
    @Column(name = "num_tw_females")
    protected int numTWFemales;
    
    @Column(name = "num_rtw_females")
    protected int numRTWFemales;
    
    @Column(name = "num_reply_females")
    protected int numRplyFemales;
    
    @Column(name = "num_tw_pages")
    protected int numTWPages;
    
    @Column(name = "num_rtw_pages")
    protected int numRTWPages;
    
    @Column(name = "num_reply_pages")
    protected int numRplyPages;
    
    
    @Column(name = "num_tw_unknown")
    protected int numTWUnknown;
    
    @Column(name = "num_rtw_unknown")
    protected int numRTWUnknown;
    
    @Column(name = "num_reply_unknown")
    protected int numRplyUnknown;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getGran() {
        return gran;
    }

    public void setGran(int gran) {
        this.gran = gran;
    }

    public int getNumTWMales() {
        return numTWMales;
    }

    public void setNumTWMales(int numTWMales) {
        this.numTWMales = numTWMales;
    }

    public int getNumRTWMales() {
        return numRTWMales;
    }

    public void setNumRTWMales(int numRTWMales) {
        this.numRTWMales = numRTWMales;
    }

    public int getNumRplyMales() {
        return numRplyMales;
    }

    public void setNumRplyMales(int numRplyMales) {
        this.numRplyMales = numRplyMales;
    }

    public int getNumTWFemales() {
        return numTWFemales;
    }

    public void setNumTWFemales(int numTWFemales) {
        this.numTWFemales = numTWFemales;
    }

    public int getNumRTWFemales() {
        return numRTWFemales;
    }

    public void setNumRTWFemales(int numRTWFemales) {
        this.numRTWFemales = numRTWFemales;
    }

    public int getNumRplyFemales() {
        return numRplyFemales;
    }

    public void setNumRplyFemales(int numRplyFemales) {
        this.numRplyFemales = numRplyFemales;
    }

    public int getNumTWPages() {
        return numTWPages;
    }

    public void setNumTWPages(int numTWPages) {
        this.numTWPages = numTWPages;
    }

    public int getNumRTWPages() {
        return numRTWPages;
    }

    public void setNumRTWPages(int numRTWPages) {
        this.numRTWPages = numRTWPages;
    }

    public int getNumRplyPages() {
        return numRplyPages;
    }

    public void setNumRplyPages(int numRplyPages) {
        this.numRplyPages = numRplyPages;
    }

    public int getNumTWUnknown() {
        return numTWUnknown;
    }

    public void setNumTWUnknown(int numTWUnknown) {
        this.numTWUnknown = numTWUnknown;
    }

    public int getNumRTWUnknown() {
        return numRTWUnknown;
    }

    public void setNumRTWUnknown(int numRTWUnknown) {
        this.numRTWUnknown = numRTWUnknown;
    }

    public int getNumRplyUnknown() {
        return numRplyUnknown;
    }

    public void setNumRplyUnknown(int numRplyUnknown) {
        this.numRplyUnknown = numRplyUnknown;
    }
    
    
    
}
