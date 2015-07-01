package com.tilab.ca.sda.consumer.tw.tot.dao.hibernate.mapping;


import com.tilab.ca.sda.sda.model.keys.GeoLocTruncKey;
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
@Table(name = "ss_stats_pre_geo_bound")
public class StatsPreGeoBound implements Serializable {

    private static final long serialVersionUID = -1L;

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

    @Column(name = "num_tw")
    private int numTw = 0;

    @Column(name = "num_rtw")
    private int numRtw = 0;

    @Column(name = "num_rply")
    private int numReply = 0;

    @Column(name = "tot_tw")
    private int totTw = 0;

    public StatsPreGeoBound() {
    }

    public StatsPreGeoBound(Date from, Date to, GeoLocTruncKey gltc, StatsCounter sgc) {
        this.from = from;
        this.to = to;
        this.latTrunc = gltc.getLatTrunc();
        this.longTrunc = gltc.getLongTrunc();
        this.numTw = sgc.getNumTw();
        this.numRtw = sgc.getNumRtw();
        this.numReply = sgc.getNumReply();
        this.totTw = numTw + numRtw + numReply;
    }

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

    public int getNumTw() {
        return numTw;
    }

    public void setNumTw(int numTw) {
        this.numTw = numTw;
    }

    public int getNumRtw() {
        return numRtw;
    }

    public void setNumRtw(int numRtw) {
        this.numRtw = numRtw;
    }

    public int getNumReply() {
        return numReply;
    }

    public void setNumReply(int numReply) {
        this.numReply = numReply;
    }

    public int getTotTw() {
        return totTw;
    }

    public void setTotTw(int totTw) {
        this.totTw = totTw;
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

}
