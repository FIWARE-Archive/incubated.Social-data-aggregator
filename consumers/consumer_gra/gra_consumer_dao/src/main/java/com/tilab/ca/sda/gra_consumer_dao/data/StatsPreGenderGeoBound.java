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
@Table(name = "precuc_geo_gender_bound")
public class StatsPreGenderGeoBound implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "lat_trunc")
    private double latTrunc;

    @Column(name = "long_trunc")
    private double longTrunc;

    @Column(name = "from_time")
    private Date from = null;

    @Column(name = "to_time")
    private Date to = null;
    
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

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
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
        return String.format("%f,%f,%s,%s,%d,%d,%d,%d",latTrunc,longTrunc,Utils.Time.date2ZonedDateTime(from).toString(),
                                Utils.Time.date2ZonedDateTime(to).toString(),
                                numMales,numFemales,numPages,numUndefined);
    }
}
