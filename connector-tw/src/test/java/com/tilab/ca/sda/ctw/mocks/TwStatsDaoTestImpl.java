package com.tilab.ca.sda.ctw.mocks;

import com.tilab.ca.sda.ctw.dao.TwStatsDao;
import com.tilab.ca.sda.ctw.data.GeoBox;
import com.tilab.ca.spark_test_lib.streaming.interfaces.ExpectedOutputHandler;
import java.util.LinkedList;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import twitter4j.Status;

public class TwStatsDaoTestImpl implements TwStatsDao,ExpectedOutputHandler{

    public List<Status> mockTwStorage;
    private final int expectedOutputSize;

    public TwStatsDaoTestImpl(int expectedOutputSize) {
        this.mockTwStorage = new LinkedList<>();
        this.expectedOutputSize=expectedOutputSize;
    }
    
    
    @Override
    public List<GeoBox> getOnMonGeo(String nodeName) throws Exception {
        return null;
    }

    @Override
    public List<String> getOnMonKeys(String nodeName) throws Exception {
        return null;
    }

    @Override
    public void saveRddData(JavaRDD<?> rdd, String dataPath, String dataRootFolderName) {
        System.out.println("called save");
        mockTwStorage.addAll((List<Status>)rdd.collect());
    }

    @Override
    public boolean isExpectedOutputFilled() {
        return mockTwStorage.size()==expectedOutputSize;
    }
    
}
