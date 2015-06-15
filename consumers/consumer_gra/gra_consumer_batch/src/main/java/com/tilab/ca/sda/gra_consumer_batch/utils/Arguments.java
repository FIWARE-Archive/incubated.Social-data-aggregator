package com.tilab.ca.sda.consumer.tw.tot.batch.utils;

import java.time.ZonedDateTime;


public class Arguments {
    
    private ZonedDateTime from=null;
    private ZonedDateTime to=null;
    private String inputDataPath=null;
    private Integer roundMode;
    private Integer granMin;

    public ZonedDateTime getFrom() {
        return from;
    }

    public void setFrom(ZonedDateTime from) {
        this.from = from;
    }

    public ZonedDateTime getTo() {
        return to;
    }

    public void setTo(ZonedDateTime to) {
        this.to = to;
    }

    public String getInputDataPath() {
        return inputDataPath;
    }

    public void setInputDataPath(String inputDataPath) {
        this.inputDataPath = inputDataPath;
    }

    public Integer getRoundMode() {
        return roundMode;
    }

    public void setRoundMode(Integer roundMode) {
        this.roundMode = roundMode;
    }

    public Integer getGranMin() {
        return granMin;
    }

    public void setGranMin(Integer granMin) {
        this.granMin = granMin;
    }

}
