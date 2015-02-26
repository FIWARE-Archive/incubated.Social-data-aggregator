
package com.tilab.ca.sda.consumer.tw.tot.stream.test.mocks;

import com.tilab.ca.sda.consumer.tw.tot.stream.TwTotConsumerProperties;


public class TwPropsTestImpl implements TwTotConsumerProperties{

    @Override
    public String defaultRoundMode() {
        return "min";
    }

    @Override
    public Integer granMin() {
        return null;
    }

    @Override
    public int roundPos() {
        return 3;
    }

    @Override
    public String sparkCleanTTL() {
        return "48000";
    }

    @Override
    public String numMaxCore() {
        return "4";
    }

    @Override
    public String checkpointDir() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer sparkBatchDurationMillis() {
       return 60000;
    }

    @Override
    public int twTotWindowDurationMillis() {
       return 60000;
    }

    @Override
    public int twTotWindowSlidingIntervalMillis() {
        return 60000;
    }

    @Override
    public String keyGeo() {
        return "geo";
    }

    @Override
    public String keyHt() {
       return "ht";
    }

    @Override
    public String busConnImplClass() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String daoImplClass() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
