package com.tilab.ca.sda.gra_consumer_dao.data;

import com.tilab.ca.sda.ctw.utils.Utils;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="gra_aggr_geo_gender")
public class StatsPreGenderGeo extends StatsPreGender{

    @Column(name = "lat_trunc")
    private double latTrunc;

    @Column(name = "long_trunc")
    private double longTrunc;

   
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

   
    @Override
    public String toString(){
        return String.format("%f,%f,%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d",latTrunc,longTrunc,Utils.Time.date2ZonedDateTime(createdAt)
                                                                                                                               .toString(),
                                                                                                gran,
                                                                                                numTWMales,numRTWMales,numRplyMales,
                                                                                                numTWFemales,numRTWFemales,numRplyFemales,
                                                                                                numTWPages,numRTWPages,numRplyPages,
                                                                                                numTWUnknown,numRTWUnknown,numRplyUnknown);
    }
}
