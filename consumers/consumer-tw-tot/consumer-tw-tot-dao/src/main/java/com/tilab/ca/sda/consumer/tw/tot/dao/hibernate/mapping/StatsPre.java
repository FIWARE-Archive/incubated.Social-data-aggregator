package com.tilab.ca.sda.consumer.tw.tot.dao.hibernate.mapping;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


@MappedSuperclass()
public class StatsPre implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    protected int id;
    
    @Column(name = "num_tw")
    protected int numTw = 0;

    @Column(name = "num_rtw")
    protected int numRtw = 0;

    @Column(name = "num_rply")
    protected int numReply = 0;

    @Column(name = "tot_tw")
    protected int totTw = 0;

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
