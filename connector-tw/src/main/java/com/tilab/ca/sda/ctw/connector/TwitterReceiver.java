/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.ctw.connector;

import com.tilab.ca.sda.ctw.Constants;
import com.tilab.ca.sda.ctw.utils.Utils;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.spark.streaming.receiver.Receiver;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;

/**
 *
 * @author Administrator
 */
public class TwitterReceiver extends Receiver<Status> {

    private static final Logger log = Logger.getLogger(TwitterReceiver.class);

    private TwitterStream twitterStream = null;
    private Configuration twStreamConf;
    private String[] trackArray=null;
    private double[][] locationFilterArray=null;
    private long[] user2FollowArray=null;

    public TwitterReceiver(TwitterReceiverBuilder trb) {
        super(trb.getStorageLevelOrDefault());
        this.twStreamConf=trb.getTwitterConfiguration();
        if(trb.isTrackDefined())
            this.trackArray=trb.getTrackArray();
        if(trb.isLocationDefined())
            this.locationFilterArray=trb.getLocationsArray();
        if(trb.areUsers2FollowDefined())
            this.user2FollowArray=trb.getUser2FollowArray();
    }

    @Override
    public void onStart() {
        try {
            log.info(String.format("[%s] CALLED ONSTART on Twitter Receiver...", Constants.TW_RECEIVER_LOG_TAG));
            log.debug(String.format("[%s] Init twitterStreamFactory...", Constants.TW_RECEIVER_LOG_TAG));
            
            TwitterStream twStream = new TwitterStreamFactory(twStreamConf).getInstance();
            
            log.debug(String.format("[%s] twitterStreamFactory initialized!", Constants.TW_RECEIVER_LOG_TAG));
            log.debug(String.format("[%s] adding status listener..", Constants.TW_RECEIVER_LOG_TAG));
            
            twStream.addListener(new StatusListener() {

                @Override
                public void onException(Exception e) {
                    log.error(String.format("[%s] Exception on twitterStream", Constants.TW_RECEIVER_LOG_TAG), e);
                    restart("Error on receiving tweets!", e);
                }

                @Override
                public void onTrackLimitationNotice(int notDelivered) {
                    log.warn(String.format("[%s] WARNING:number of statuses matching keywords but not delivered => %d",Constants.TW_RECEIVER_LOG_TAG,
                            notDelivered));
                }

                @Override
                public void onStatus(Status status) {
                    log.debug(String.format("[%s] new status received! storing on spark..", Constants.TW_RECEIVER_LOG_TAG));
                    store(status);
                }

                @Override
                public void onStallWarning(StallWarning stallW) {
                    log.warn(String.format("[%s] WARNING: Received Stall Warning message: %s" ,Constants.TW_RECEIVER_LOG_TAG,stallW.getMessage()));
                }

                @Override
                public void onScrubGeo(long arg0, long arg1) {}

                @Override
                public void onDeletionNotice(StatusDeletionNotice arg0) {}
            });

            log.debug(String.format("[%s] Creating filterquery", Constants.TW_RECEIVER_LOG_TAG));
            FilterQuery fq = new FilterQuery();
            if (trackArray!=null) {
                log.debug(String.format("[%s] added keys to track", Constants.TW_RECEIVER_LOG_TAG));
                fq.track(trackArray);
            }
            if (locationFilterArray!=null) {
                log.info(String.format("[%s] added geolocations to track", Constants.TW_RECEIVER_LOG_TAG));
                fq.locations(locationFilterArray);
            }
            if (user2FollowArray!=null) {
                log.debug(String.format("[%s] Added users to track", Constants.TW_RECEIVER_LOG_TAG));
                fq.follow(user2FollowArray);
            }
            twStream.filter(fq);
            log.debug(String.format("[%s] Setting twitterStream..", Constants.TW_RECEIVER_LOG_TAG));

            setTwitterStream(twStream);

            log.info(String.format("[%s] Twitter Stream started", Constants.TW_RECEIVER_LOG_TAG));

        } catch (Throwable t) {
            log.error(String.format("[%s] Error starting twitter stream", Constants.TW_RECEIVER_LOG_TAG), t);
            restart("Error starting twitter stream", t);
        }
    }

    @Override
    public void onStop() {
        log.info(String.format("[%s] CALLED ONSTOP()", Constants.TW_RECEIVER_LOG_TAG));
        setTwitterStream(null);
        log.info(String.format("[%s] Twitter Stream stopped", Constants.TW_RECEIVER_LOG_TAG));
    }

    protected synchronized void setTwitterStream(TwitterStream tw) {
        if (twitterStream != null) {
            twitterStream.shutdown();
        }
        twitterStream = tw;
    }

}
