/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.ctw.utils.stream;


import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.api.java.JavaStreamingContextFactory;

/**
 *
 * @author Administrator
 */
public class SparkStreamingManager {
   
    private static final Logger log=Logger.getLogger(SparkStreamingManager.class);
    private static final String STREAMING_MANAGER_LOG_TAG="SPARK-STREAMING-MANAGER";
    
    private JavaStreamingContext jssc=null;
    private SparkConf conf=null;
    private int batchDurationMillis;
    private String checkpointPath=null;
    
    private SparkOperation sparkOperation=null;

    
    public static SparkStreamingManager $newStreamingManager(){
        return new SparkStreamingManager();
    }
    
    public SparkStreamingManager withBatchDurationMillis(int batchDurationMillis){
        this.batchDurationMillis=batchDurationMillis;
        return this;
    }
    
    public SparkStreamingManager withSparkConf(SparkConf conf){
        this.conf=conf;
        return this;
    }
    
    public SparkStreamingManager withCheckpointPath(String checkPointPath){
        this.checkpointPath=checkPointPath;
        return this;
    }
	
   
    private JavaStreamingContext createContext(SparkOperation operation) throws Exception{
        log.info(String.format("[%s] Initializing spark streaming context with batchDurationMillis %d ...",STREAMING_MANAGER_LOG_TAG,
                    batchDurationMillis));
        log.info(String.format("[%s] Creating new JavaStreamingContext...",STREAMING_MANAGER_LOG_TAG));	
        Duration batchInterval = new Duration(batchDurationMillis);
        
        JavaStreamingContext jsscT=new JavaStreamingContext(conf,batchInterval);
        jsscT.checkpoint(checkpointPath);
        
        //execute the operation
        this.sparkOperation=operation;
        operation.execute(jsscT);
        
        return jsscT;
    }
    
    /**
     * Create the JavaStreamingContext and start the application 
     * @param operation SparkOperation containing the code the streaming application has to execute 
     */
    public void startSparkStream(SparkOperation operation){
            try{
                $startSparkStream(operation);
            }catch(Exception e){
                 logErrorAndRestartDriver(e);
            }
    }
    
    private void logErrorAndRestartDriver(Exception e){
        log.error(String.format("[%s] Exception on driver: ",STREAMING_MANAGER_LOG_TAG), e);
        log.info(String.format("[%s] Trying to restart driver..",STREAMING_MANAGER_LOG_TAG));
        try {
            restartSparkStreaming();
        } catch (Exception ex) {
            log.error(String.format("[%s] Failed to restart driver.",STREAMING_MANAGER_LOG_TAG), e);
        }
    }
    
    private void $startSparkStream(SparkOperation operation){
            log.info(String.format("[%s] Starting streaming app...",STREAMING_MANAGER_LOG_TAG));
            
            JavaStreamingContextFactory cntxFactory=() -> {
                try {
                    return createContext(operation);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            };
            jssc = JavaStreamingContext.getOrCreate(checkpointPath, cntxFactory);
            jssc.start();
            jssc.awaitTermination();
    }
    
	
    public void tearDownSparkStreaming(){
            log.info(String.format("[%s] Stopping spark streaming context...",STREAMING_MANAGER_LOG_TAG));
            jssc.stop(true,true);
            jssc=null;
            log.info(String.format("[%s] Cleaning spark driver port/host...",STREAMING_MANAGER_LOG_TAG));
            System.clearProperty(SparkStreamingSystemSettings.SPARK_DRIVER_PORT_PROPERTY);
            System.clearProperty(SparkStreamingSystemSettings.SPARK_DRIVER_HOST_PROPERTY);
            log.info(String.format("[%s] port/host Cleaned.",STREAMING_MANAGER_LOG_TAG));
    }
	
    public void restartSparkStreaming() throws Exception{
        log.info(String.format("[%s] Tearing down spark streaming..",STREAMING_MANAGER_LOG_TAG));
        tearDownSparkStreaming();
        //log.info(String.format("[%s] Setting up new StreamingContext...",STREAMING_MANAGER_LOG_TAG));
        //setUpSparkStreaming();
        log.info(String.format("[%s] Restarting spark streaming...",STREAMING_MANAGER_LOG_TAG));
        startSparkStream(sparkOperation);
    }
    
}
