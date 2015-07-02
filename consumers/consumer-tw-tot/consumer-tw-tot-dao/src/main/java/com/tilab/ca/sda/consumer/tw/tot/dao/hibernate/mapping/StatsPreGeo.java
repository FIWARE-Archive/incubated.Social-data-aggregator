package com.tilab.ca.sda.consumer.tw.tot.dao.hibernate.mapping;

import com.tilab.ca.sda.sda.model.keys.GeoLocTruncTimeKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.StatsCounter;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ss_stats_pre_geo")
public class StatsPreGeo extends StatsPre {

    private static final long serialVersionUID = -1968411105206976328L;

    @Column(name = "lat_trunc")
    private double latTrunc;

    @Column(name = "long_trunc")
    private double longTrunc;

    @Column(name = "created_at")
    private Date createdAt = null;

    private int gran;


    public StatsPreGeo() {
    }

    public StatsPreGeo(GeoLocTruncTimeKey gltc, StatsCounter sgc, int gran) {
        super();
        this.latTrunc = gltc.getGeoLocTruncKey().getLatTrunc();
        this.longTrunc = gltc.getGeoLocTruncKey().getLongTrunc();
        this.createdAt = gltc.getDate();
        this.numTw = sgc.getNumTw();
        this.numRtw = sgc.getNumRtw();
        this.numReply = sgc.getNumReply();
        this.totTw = numTw + numRtw + numReply;
        this.gran = gran;
    }

    public StatsPreGeo(double latTrunc, double longTrunc, Date createdAt,
            int numTw, int numRtw, int numReply, int totTw,int gran) {
        super();
        this.latTrunc = latTrunc;
        this.longTrunc = longTrunc;
        this.createdAt = createdAt;
        this.numTw = numTw;
        this.numRtw = numRtw;
        this.numReply = numReply;
        this.totTw = totTw;
        this.gran=gran;
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

}
