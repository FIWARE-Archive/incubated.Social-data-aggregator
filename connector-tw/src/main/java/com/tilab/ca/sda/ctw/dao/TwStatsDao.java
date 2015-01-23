package com.tilab.ca.sda.ctw.dao;

import com.tilab.ca.sda.ctw.data.GeoBox;
import java.io.Serializable;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;

public interface TwStatsDao extends Serializable{

	public List<GeoBox> getOnMonGeo(String nodeName) throws Exception;

	public List<String> getOnMonKeys(String nodeName) throws Exception;
        
        public List<Long> getOnMonUsers(String nodeName) throws Exception;
        
        public void saveRddData(JavaRDD<?> rdd,String dataPath,String dataRootFolderName);

}
