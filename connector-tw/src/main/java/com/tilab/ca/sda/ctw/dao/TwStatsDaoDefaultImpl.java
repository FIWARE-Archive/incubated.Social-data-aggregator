package com.tilab.ca.sda.ctw.dao;

import com.tilab.ca.hibutils.HibQueryExecutor;
import com.tilab.ca.sda.ctw.data.GeoBox;
import com.tilab.ca.sda.ctw.hibernate.TwStatsSession;
import com.tilab.ca.sda.ctw.hibernate.mapping.OnMonitoringGeo;
import com.tilab.ca.sda.ctw.hibernate.mapping.OnMonitoringKey;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.hibernate.criterion.Restrictions;

public class TwStatsDaoDefaultImpl implements TwStatsDao {
    
    private static final Logger log = Logger.getLogger(TwStatsDaoDefaultImpl.class);
    private static final String TW_STATS_DAO_DEFAULT_IMPL_LOG_TAG="TWSTATS-DA0-DEFAULT";

    private static final String TIME_STR_TREE_PATH="yyyy-MM-dd-HH-mm";
    private static final DateTimeFormatter PATH_FORMATTER = DateTimeFormatter.ofPattern(TIME_STR_TREE_PATH);
    
    @Override
    public List<String> getOnMonKeys(String nodeName) throws Exception {
       
        return new HibQueryExecutor<String>()
                .select("key")
                .from(OnMonitoringKey.class)
                .retClass(String.class)
                .where(Restrictions.eq("monitorFromNode", nodeName))
                .listResult(TwStatsSession.getSessionFactory());
    }

    @Override
    public List<GeoBox> getOnMonGeo(String nodeName) throws Exception {

        return new HibQueryExecutor<OnMonitoringGeo>()
                .from(OnMonitoringGeo.class)
                .where(Restrictions.eq("monitorFromNode", nodeName))
                .listResult(TwStatsSession.getSessionFactory())
                .stream()
                .map((onMonGeoElem) -> new GeoBox(onMonGeoElem.getLatitudeFrom(),onMonGeoElem.getLatitudeTo(),
                                                  onMonGeoElem.getLongitudeFrom(),onMonGeoElem.getLongitudeTo()))
                .collect(Collectors.toList());
    }

    @Override
    public void saveRddData(JavaRDD<?> rdd, String dataPath, String dataRootFolderName) {
        log.debug(String.format("[%s] START saveRddData",TW_STATS_DAO_DEFAULT_IMPL_LOG_TAG));
        LocalDateTime today = LocalDateTime.now();
        
        String treePath = String.format("%s-%s-%s",
                dataPath.trim(),
                dataRootFolderName.trim(),
                today.format(PATH_FORMATTER)).replace("-", File.separator);
        
        log.info(String.format("[%s] saving %d objs on path %s",TW_STATS_DAO_DEFAULT_IMPL_LOG_TAG,rdd.count(),treePath));
        rdd.saveAsTextFile(treePath);
        log.debug(String.format("[%s] END saveRddData",TW_STATS_DAO_DEFAULT_IMPL_LOG_TAG));
    }
}
