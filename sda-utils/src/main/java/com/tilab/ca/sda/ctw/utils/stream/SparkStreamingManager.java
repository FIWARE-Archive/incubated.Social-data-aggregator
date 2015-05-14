/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.ctw.utils.stream;


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
	
    /*
    public SparkStreamingManager setUpSparkStreaming(){
            log.info(String.format("[%s] Initializing spark streaming context with batchDurationMillis %d ...",STREAMING_MANAGER_LOG_TAG,
                    batchDurationMillis));	
 
            JavaStreamingContextFactory cntxFactory=() -> {
                return getNewJavaStreamingContext();
            };
            
            // Get JavaStreamingContext from checkpoint data or create a new one
            try{
                jssc = JavaStreamingContext.getOrCreate(checkpointPath, cntxFactory);
            }catch(Exception se){
                if(se instanceof SparkException){
                    log.error(String.format("[%s] Failed to read checkpoints dir ",STREAMING_MANAGER_LOG_TAG),se);
                    jssc=getNewJavaStreamingContext();
                }else{
                    throw se;
                }
            }
            log.info(String.format("[%s] Spark streaming context initialized",STREAMING_MANAGER_LOG_TAG));	
            return this;
    }*/
    
    private JavaStreamingContext setUpSparkStreaming(SparkOperation operation) throws Exception{
        log.info(String.format("[%s] Initializing spark streaming context with batchDurationMillis %d ...",STREAMING_MANAGER_LOG_TAG,
                    batchDurationMillis));
        log.info(String.format("[%s] Creating new JavaStreamingContext...",STREAMING_MANAGER_LOG_TAG));	
        Duration batchInterval = new Duration(batchDurationMillis);
        
        JavaStreamingContext jsscT=new JavaStreamingContext(conf,batchInterval);
        jsscT.checkpoint(checkpointPath);
        
        //execute the operation
        this.sparkOperation=operation;
        operation.execute(jssc);
        
        return jsscT;
    }
    
    /**
     * Create the JavaStreamingContext and start the application 
     * @param operation SparkOperation containing the code the streaming application has to execute 
     */
    public void startSparkStream(SparkOperation operation){
            log.info(String.format("[%s] Starting streaming app...",STREAMING_MANAGER_LOG_TAG));
            
            JavaStreamingContextFactory cntxFactory=() -> {
                try {
                    return setUpSparkStreaming(operation);
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
        log.info(String.format("[%s] Setting up new StreamingContext...",STREAMING_MANAGER_LOG_TAG));
        //setUpSparkStreaming();
        log.info(String.format("[%s] Restarting spark streaming...",STREAMING_MANAGER_LOG_TAG));
        startSparkStream(sparkOperation);
    }
    
}
