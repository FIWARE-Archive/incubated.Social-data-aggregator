package com.tilab.ca.sda.ctw.mocks;

import com.tilab.ca.sda.ctw.TwStreamConnectorProperties;

public class TwCollectorTestProps implements TwStreamConnectorProperties{

    @Override
    public int proxyPort() {return 0;}

    @Override
    public String proxyHost() {return null;}

    @Override
    public String twConsumerKey() {return null;}

    @Override
    public String twConsumerSecret() {return null;}

    @Override
    public String twToken(){return null;}

    @Override
    public String twTokenSecret() {return null;}

    @Override
    public String langFilter() {return null;}

    @Override
    public String nodeName() { return "testNode";}

    @Override
    public int serverPort() {return 8889;}

    @Override
    public String dataOutputFolder() {return "dataOutFolder";}

    @Override
    public String dataRootFolder() {return "rootFolder";}

    @Override
    public String sparkCleanTTL() {return "";}

    @Override
    public String numMaxCore() {return "2";}

    @Override
    public String checkpointDir() {return "checkpoints";}

    @Override
    public int sparkBatchDurationMillis() {return 1000;}

    @Override
    public int twitterInserterWindowDuration() {return 60000;}

    @Override
    public int twitterInserterWindowSlidingInterval() {return 60000;}

    @Override
    public String daoClass() {return null;}

    @Override
    public String brokersList() {
        return "";
    }

    @Override
    public String kafkaSerializationClass() {
        return null;
    }

    @Override
    public int kafkaRequiredAcks() {return 1;}

    @Override
    public int maxTotalConnections() {return 10;}

    @Override
    public int maxIdleConnections() {return 10;}

    @Override
    public int savePartitions() {
        return 3;
    }
    
    @Override
    public String customProducerFactoryImpl(){
        return "com.tilab.ca.sda.ctw.mocks.ProducerFactoryTestImpl";
    }


    
}
