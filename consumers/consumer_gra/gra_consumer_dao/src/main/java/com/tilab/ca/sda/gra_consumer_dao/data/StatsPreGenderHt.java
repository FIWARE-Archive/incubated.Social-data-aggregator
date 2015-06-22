package com.tilab.ca.sda.gra_consumer_dao.data;

import com.tilab.ca.sda.ctw.utils.Utils;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "precuc_ht_gender")
public class StatsPreGenderHt extends StatsPreGender {

    @Column(name = "ht")
    private String ht;

    public String getHt() {
        return ht;
    }

    public void setHt(String ht) {
        this.ht = ht;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", ht, Utils.Time.date2ZonedDateTime(createdAt).toString(),
                numTWMales, numRTWMales, numRplyMales,
                numTWFemales, numRTWFemales, numRplyFemales,
                numTWPages, numRTWPages, numRplyPages,
                numTWUnknown, numRTWUnknown, numRplyUnknown);
    }
}
