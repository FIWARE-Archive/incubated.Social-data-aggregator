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
@Table(name = "precuc_ht_gender_bound")
public class StatsPreGenderHtBound implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "from_time")
    private Date from;

    @Column(name = "to_time")
    private Date to;

    @Column(name = "ht")
    private String ht;

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

    public String getHt() {
        return ht;
    }

    public void setHt(String ht) {
        this.ht = ht;
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
        return String.format("%s,%s,%s,%d,%d,%d,%d",ht,Utils.Time.date2ZonedDateTime(from).toString(),
                                Utils.Time.date2ZonedDateTime(to).toString(),
                                numMales,numFemales,numPages,numUndefined);
    }
    
}
