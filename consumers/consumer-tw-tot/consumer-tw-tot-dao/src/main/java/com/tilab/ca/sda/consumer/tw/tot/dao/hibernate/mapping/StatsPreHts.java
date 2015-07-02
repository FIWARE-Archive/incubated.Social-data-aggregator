package com.tilab.ca.sda.consumer.tw.tot.dao.hibernate.mapping;


import com.tilab.ca.sda.sda.model.keys.DateHtKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.StatsCounter;
import com.tilab.ca.sda.ctw.utils.Utils;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ss_stats_pre_hts")
public class StatsPreHts extends StatsPre {

    private static final long serialVersionUID = -1545245637924683920L;

    @Column(name = "hash_tag")
    private String hashTag = null;

    @Column(name = "created_at")
    private Date createdAt = null;
    
    private int gran;


    public StatsPreHts() {
        super();
    }

    public StatsPreHts(DateHtKey dateHtKey, StatsCounter sgc,int gran) {
        super();
        this.hashTag = dateHtKey.getHt();
        this.createdAt = dateHtKey.getDate();
        this.numTw = sgc.getNumTw();
        this.numRtw = sgc.getNumRtw();
        this.numReply = sgc.getNumReply();
        this.totTw = numTw + numRtw + numReply;
        this.gran=gran;
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

    public int getGran() {
        return gran;
    }

    public void setGran(int gran) {
        this.gran = gran;
    }
    
    @Override
    public String toString(){
        return String.format("%s,%s,%d,%d,%d,%d,%d",hashTag,Utils.Time.date2ZonedDateTime(createdAt).toString(),
                                                    gran,numTw,numRtw,numReply);
    }

}
