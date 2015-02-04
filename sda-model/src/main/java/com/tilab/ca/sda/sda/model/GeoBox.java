package com.tilab.ca.sda.sda.model;


public class GeoBox {
    
    private double latitudeFrom;
    private double latitudeTo;
    private double longitudeFrom;
    private double longitudeTo;
    
    public GeoBox(){}

    public GeoBox(double latitudeFrom, double latitudeTo, double longitudeFrom, double longitudeTo) {
        this.latitudeFrom = latitudeFrom;
        this.latitudeTo = latitudeTo;
        this.longitudeFrom = longitudeFrom;
        this.longitudeTo = longitudeTo;
    }

    public double getLatitudeFrom() {
        return latitudeFrom;
    }

    public void setLatitudeFrom(double latitudeFrom) {
        this.latitudeFrom = latitudeFrom;
    }

    public double getLatitudeTo() {
        return latitudeTo;
    }

    public void setLatitudeTo(double latitudeTo) {
        this.latitudeTo = latitudeTo;
    }

    public double getLongitudeFrom() {
        return longitudeFrom;
    }

    public void setLongitudeFrom(double longitudeFrom) {
        this.longitudeFrom = longitudeFrom;
    }

    public double getLongitudeTo() {
        return longitudeTo;
    }

    public void setLongitudeTo(double longitudeTo) {
        this.longitudeTo = longitudeTo;
    }
    
    
}
