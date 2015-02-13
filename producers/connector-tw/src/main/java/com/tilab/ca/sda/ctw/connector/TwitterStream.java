package com.tilab.ca.sda.ctw.connector;

import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import twitter4j.Status;

public class TwitterStream {

    public static JavaDStream<Status> createStream(JavaStreamingContext jssc, TwitterReceiverBuilder twRecBuilder) {
        TwitterReceiver twrec = twRecBuilder.build();

        return jssc.receiverStream(twrec);
    }
}
