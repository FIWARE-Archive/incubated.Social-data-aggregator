package com.tilab.ca.sda.gra_consumer_dao.data;

import com.tilab.ca.sda.ctw.utils.Utils;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "gra_aggr_ht_gender_bound")
public class StatsPreGenderHtBound extends StatsPreGender {

    @Column(name = "from_time")
    private Date from;

    @Column(name = "to_time")
    private Date to;

    @Column(name = "ht")
    private String ht;

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

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", ht, Utils.Time.date2ZonedDateTime(from).toString(),
                Utils.Time.date2ZonedDateTime(to).toString(),
                numTWMales, numRTWMales, numRplyMales,
                numTWFemales, numRTWFemales, numRplyFemales,
                numTWPages, numRTWPages, numRplyPages,
                numTWUnknown, numRTWUnknown, numRplyUnknown);
    }

}
