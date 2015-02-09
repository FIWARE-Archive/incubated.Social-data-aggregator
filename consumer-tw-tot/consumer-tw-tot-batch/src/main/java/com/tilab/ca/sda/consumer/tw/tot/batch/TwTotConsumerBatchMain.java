/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.consumer.tw.tot.batch;

import com.tilab.ca.sda.consumer.tw.tot.batch.utils.Arguments;
import com.tilab.ca.sda.consumer.tw.tot.batch.utils.CommandLineArgs;
import com.tilab.ca.sda.consumer.tw.tot.batch.utils.TwTotConsumerProperties;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;



public class TwTotConsumerBatchMain {
    
    private static final Logger log=Logger.getLogger(TwTotConsumerBatchMain.class);
    
    
    public void main(String[] args){
        
        log.info("Start Tw tot Consumer Batch");
        log.info("Parsing commandline arguments...");
        try {
            Arguments arguments=CommandLineArgs.parseCommandLineArgs(args);
            TwTotConsumerProperties twProps=ConfigFactory.create(TwTotConsumerProperties.class);
            
            SparkConf conf=new SparkConf().setAppName(twProps.appName());
	    JavaSparkContext sc=new JavaSparkContext(conf);
            String inputDataPath= StringUtils.isBlank(arguments.getInputDataPath())?twProps.defaultInputDataPath():arguments.getInputDataPath();
            if(StringUtils.isBlank(inputDataPath))
                throw new IllegalArgumentException("Input file cannot be blank. Please provide it on the properties file or by commandline argument");
            
	    JavaRDD<String> tweetsRdd=sc.textFile(inputDataPath, twProps.minPartitions());
            
        } catch (Exception ex) {
            log.error(ex);
        }
    }
}
