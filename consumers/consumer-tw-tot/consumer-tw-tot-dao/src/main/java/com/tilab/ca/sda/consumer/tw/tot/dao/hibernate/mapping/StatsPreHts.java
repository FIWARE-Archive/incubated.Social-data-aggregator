package com.tilab.ca.sda.consumer.tw.tot.dao.hibernate.mapping;


import com.tilab.ca.sda.sda.model.keys.DateHtKey;
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
@Table(name = "ss_stats_pre_hts")
public class StatsPreHts implements Serializable {

    private static final long serialVersionUID = -1545245637924683920L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "hash_tag")
    private String hashTag = null;

    @Column(name = "created_at")
    private Date createdAt = null;

    @Column(name = "num_tw")
    private int numTw = 0;

    @Column(name = "num_rtw")
    private int numRtw = 0;

    @Column(name = "num_rply")
    private int numReply = 0;

    @Column(name = "tot_tw")
    private int totTw = 0;

    public StatsPreHts() {
        super();
    }

    public StatsPreHts(DateHtKey dateHtKey, StatsCounter sgc) {
        super();
        this.hashTag = dateHtKey.getHt();
        this.createdAt = dateHtKey.getDate();
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

    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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

}
