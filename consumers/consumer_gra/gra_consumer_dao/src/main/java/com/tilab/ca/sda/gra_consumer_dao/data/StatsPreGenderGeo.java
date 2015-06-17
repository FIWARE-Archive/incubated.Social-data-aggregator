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
@Table(name="precuc_geo_gender")
public class StatsPreGenderGeo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "lat_trunc")
    private double latTrunc;

    @Column(name = "long_trunc")
    private double longTrunc;

    @Column(name = "aggr_time")
    private Date createdAt = null;
    
    private int gran;
    
    @Column(name = "num_males")
    private int numMales;
    
    @Column(name = "num_females")
    private int numFemales;
    
    @Column(name = "num_pages")
    private int numPages;
    
    @Column(name = "num_undefined")
    private int numUndefined;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatTrunc() {
        return latTrunc;
    }

    public void setLatTrunc(double latTrunc) {
        this.latTrunc = latTrunc;
    }

    public double getLongTrunc() {
        return longTrunc;
    }

    public void setLongTrunc(double longTrunc) {
        this.longTrunc = longTrunc;
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

    public int getNumUndefined() {
        return numUndefined;
    }

    public void setNumUndefined(int numUndefined) {
        this.numUndefined = numUndefined;
    }

    public int getNumPages() {
        return numPages;
    }

    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }
    
   
    @Override
    public String toString(){
        return String.format("%f,%f,%s,%d,%d,%d,%d,%d",latTrunc,longTrunc,Utils.Time.date2ZonedDateTime(createdAt).toString(),gran,numMales,numFemales,numPages);
    }
}
