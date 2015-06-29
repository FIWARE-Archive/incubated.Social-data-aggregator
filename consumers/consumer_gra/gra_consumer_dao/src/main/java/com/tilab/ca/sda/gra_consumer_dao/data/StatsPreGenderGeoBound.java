package com.tilab.ca.sda.gra_consumer_dao.data;

import com.tilab.ca.sda.ctw.utils.Utils;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "gra_aggr_geo_gender_bound")
public class StatsPreGenderGeoBound extends StatsPreGender {

    @Column(name = "lat_trunc")
    private double latTrunc;

    @Column(name = "long_trunc")
    private double longTrunc;

    @Column(name = "from_time")
    private Date from = null;

    @Column(name = "to_time")
    private Date to = null;

   

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


    @Override
    public String toString() {
        return String.format("%f,%f,%s,%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", latTrunc, longTrunc, Utils.Time.date2ZonedDateTime(from).toString(),
                Utils.Time.date2ZonedDateTime(to).toString(),
                numTWMales, numRTWMales, numRplyMales,
                numTWFemales, numRTWFemales, numRplyFemales,
                numTWPages, numRTWPages, numRplyPages,
                numTWUnknown, numRTWUnknown, numRplyUnknown);
    }
}
