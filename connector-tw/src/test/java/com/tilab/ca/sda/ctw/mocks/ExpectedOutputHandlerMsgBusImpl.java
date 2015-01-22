package com.tilab.ca.sda.ctw.mocks;

import com.google.gson.Gson;
import com.tilab.ca.sda.ctw.mocks.ProducerFactoryTestImpl.SendContent;
import com.tilab.ca.spark_test_lib.streaming.interfaces.ExpectedOutputHandler;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class ExpectedOutputHandlerMsgBusImpl<K, V> implements ExpectedOutputHandler,Serializable {

    private final int numExpectedOutput;
    public List<SendContent<K, V>> outputList;

    public ExpectedOutputHandlerMsgBusImpl(int numExpectedOutput) {
        this.numExpectedOutput = numExpectedOutput;
        outputList = new LinkedList<>();
    }

    public void addOutputItem(String output){
        SendContent sc=new Gson().fromJson(output, SendContent.class);
        addOutputItem(sc);
    }
    public void addOutputItem(SendContent<K, V> sc) {
        outputList.add(sc);
    }

    @Override
    public boolean isExpectedOutputFilled() {
        boolean filled=(numExpectedOutput == outputList.size());
        return filled;
    }

}
