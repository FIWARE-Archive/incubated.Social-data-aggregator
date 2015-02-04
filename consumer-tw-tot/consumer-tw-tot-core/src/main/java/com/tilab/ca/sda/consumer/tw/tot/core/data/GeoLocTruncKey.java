package com.tilab.ca.sda.consumer.tw.tot.core.data;

import java.io.Serializable;

public class GeoLocTruncKey implements Serializable {

    private static final long serialVersionUID = -4359168969696352498L;
    private double latTrunc;
    private double longTrunc;

    public GeoLocTruncKey(double latTrunc, double longTrunc) {
        this.latTrunc = latTrunc;
        this.longTrunc = longTrunc;
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.latTrunc) ^ (Double.doubleToLongBits(this.latTrunc) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.longTrunc) ^ (Double.doubleToLongBits(this.longTrunc) >>> 32));
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
        final GeoLocTruncKey other = (GeoLocTruncKey) obj;
        if (Double.doubleToLongBits(this.latTrunc) != Double.doubleToLongBits(other.latTrunc)) {
            return false;
        }
        if (Double.doubleToLongBits(this.longTrunc) != Double.doubleToLongBits(other.longTrunc)) {
            return false;
        }
        return true;
    }
}
