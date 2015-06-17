package com.tilab.ca.sda.gra_consumer_dao.data;

import com.tilab.ca.sda.ctw.utils.Utils;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="precuc_ht_gender")
public class StatsPreGenderHt implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "aggr_time")
    private Date createdAt;

    @Column(name = "ht")
    private String ht;

    @Column(name = "gran")
    private int gran;

    @Column(name = "num_male")
    private int numMales;

    @Column(name = "num_female")
    private int numFemales;

    @Column(name = "num_non_human")
    private int numPages;

    @Column(name = "num_undefined")
    private int numUndefined;

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

    public String getHt() {
        return ht;
    }

    public void setHt(String ht) {
        this.ht = ht;
    }

    public int getGran() {
        return gran;
    }

    public void setGran(int gran) {
        this.gran = gran;
    }

    public int getNumMales() {
        return numMales;
    }

    public void setNumMales(int numMales) {
        this.numMales = numMales;
    }

    public int getNumFemales() {
        return numFemales;
    }

    public void setNumFemales(int numFemales) {
        this.numFemales = numFemales;
    }

    public int getNumPages() {
        return numPages;
    }

    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }

    public int getNumUndefined() {
        return numUndefined;
    }

    public void setNumUndefined(int numUndefined) {
        this.numUndefined = numUndefined;
    }
    
    @Override
    public String toString(){
        return String.format("%s,%s,%s,%d,%d,%d,%d",ht,Utils.Time.date2ZonedDateTime(createdAt).toString(),
                                numMales,numFemales,numPages,numUndefined);
    }
}
