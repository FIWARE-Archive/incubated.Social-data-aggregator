package com.tilab.ca.sda.consumer.tw.tot.core.data;


import com.tilab.ca.sda.sda.model.GeoStatus;
import com.tilab.ca.sda.sda.model.HtsStatus;
import java.io.Serializable;

public class StatsCounter implements Serializable {

    private static final long serialVersionUID = 4343203900474401129L;
    private int numTw = 0;
    private int numRtw = 0;
    private int numReply = 0;

    public StatsCounter() {
    }

    public StatsCounter(HtsStatus status) {
        if (status.isRetweet()) {
            numRtw = 1;
        } else if (status.isReply()) {
            numReply = 1;
        } else {
            numTw = 1;
        }
    }

    public StatsCounter(GeoStatus status) {
        if (status.isRetweet()) {
            numRtw = 1;
        } else if (status.isReply()) {
            numReply = 1;
        } else {
            numTw = 1;
        }
    }

    public StatsCounter(boolean isRetweet, boolean isReply) {
        if (isRetweet) {
            numRtw = 1;
        } else if (isReply) {
            numReply = 1;
        } else {
            numTw = 1;
        }
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

    public StatsCounter sum(StatsCounter sgc) {
        this.numReply += sgc.getNumReply();
        this.numRtw += sgc.getNumRtw();
        this.numTw += sgc.getNumTw();

        return this;
    }

}
