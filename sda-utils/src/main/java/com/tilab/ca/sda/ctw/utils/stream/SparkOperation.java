/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.ctw.utils.stream;

import org.apache.spark.streaming.api.java.JavaStreamingContext;

/**
 *
 * @author Administrator
 */
public interface SparkOperation {
	
	public void execute(JavaStreamingContext jssc) throws Exception;
}
