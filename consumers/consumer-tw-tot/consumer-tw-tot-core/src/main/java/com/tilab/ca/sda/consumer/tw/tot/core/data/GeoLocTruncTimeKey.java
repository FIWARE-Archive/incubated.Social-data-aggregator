package com.tilab.ca.sda.consumer.tw.tot.core.data;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;


public class GeoLocTruncTimeKey implements Serializable{
    
    private Date date;
    private GeoLocTruncKey geoLocTruncKey;
    
    public GeoLocTruncTimeKey(Date date,double latTrunc, double longTrunc) {
        this.date=date;
        geoLocTruncKey.setLatTrunc(latTrunc);
        geoLocTruncKey.setLongTrunc(longTrunc);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public GeoLocTruncKey getGeoLocTruncKey() {
        return geoLocTruncKey;
    }

    public void setGeoLocTruncKey(GeoLocTruncKey geoLocTruncKey) {
        this.geoLocTruncKey = geoLocTruncKey;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.date);
        hash = 37 * hash + Objects.hashCode(this.geoLocTruncKey);
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
        final GeoLocTruncTimeKey other = (GeoLocTruncTimeKey) obj;
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.geoLocTruncKey, other.geoLocTruncKey)) {
            return false;
        }
        return true;
    }
}
