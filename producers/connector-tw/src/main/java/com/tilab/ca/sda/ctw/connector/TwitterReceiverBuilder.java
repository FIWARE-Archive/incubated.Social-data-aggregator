package com.tilab.ca.sda.ctw.connector;

import com.tilab.ca.sda.ctw.utils.Utils;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.spark.storage.StorageLevel;
import twitter4j.conf.Configuration;

public class TwitterReceiverBuilder implements Serializable {

    private static final long serialVersionUID = -7685533808179201104L;
    private Configuration twitterConf = null;
    private StorageLevel storageLevel = null;
    private List<String> trackFilter = null;
    private List<Double[]> locationFilter = null;
    private List<Long> user2Follow = null;
    
    private boolean emptyKeys = false;

    public TwitterReceiverBuilder() {
        trackFilter = new LinkedList<>();
        locationFilter = new LinkedList<>();
        user2Follow = new LinkedList<>();
    }

    public TwitterReceiver build() {
        if (twitterConf == null) {
            throw new IllegalStateException("TwitterReceiver build failure: missing ConfigurationBuilder.");
        }
        if (!areFiltersSetted()) {
            throw new IllegalStateException("TwitterReceiver build failure: no filters found");
        }
        return new TwitterReceiver(this);
    }

    public boolean areFiltersSetted() {
        return Utils.isNotNullOrEmpty(trackFilter)
                || Utils.isNullOrEmpty(locationFilter) || Utils.isNullOrEmpty(user2Follow);
    }

    public TwitterReceiverBuilder twitterConfiguration(Configuration twitterConf) {
        this.twitterConf = twitterConf;
        return this;
    }

    public TwitterReceiverBuilder trackList(List<String> trackList) {
        trackFilter.addAll(trackList);
        return this;
    }

    public TwitterReceiverBuilder htsTrackList(List<String> htsList) {
        trackFilter.addAll(htsList.stream()
                .map((ht) -> String.format("#%s", ht)).collect(Collectors.toList()));
        return this;
    }

    public TwitterReceiverBuilder addLocationFilter(double latitudeFrom, double longitudeFrom,
            double latitudeTo, double longitudeTo) {
        locationFilter.add(new Double[]{longitudeFrom, latitudeFrom});
        locationFilter.add(new Double[]{longitudeTo, latitudeTo});
        return this;
    }

    public TwitterReceiverBuilder users2Follow(List<Long> usersList) {
        user2Follow.addAll(usersList);
        return this;
    }

    public TwitterReceiverBuilder addUser2Follow(Long userId) {
        user2Follow.add(userId);
        return this;
    }

    public TwitterReceiverBuilder storageLevel(StorageLevel storageLevel) {
        this.storageLevel = storageLevel;
        return this;
    }
    
    public TwitterReceiverBuilder withEmptyKeys(){
        this.emptyKeys = true;
        return this;
    }

    public Configuration getTwitterConfiguration() {
        return twitterConf;
    }

    public StorageLevel getStorageLevelOrDefault() {
        StorageLevel sl = StorageLevel.MEMORY_AND_DISK_2();
        if (storageLevel != null) {
            sl = storageLevel;
        }

        return sl;
    }

    public boolean isTrackDefined() {
        return !trackFilter.isEmpty();
    }

    public boolean isLocationDefined() {
        return !locationFilter.isEmpty();
    }

    public boolean areUsers2FollowDefined() {
        return !user2Follow.isEmpty();
    }

    public String[] getTrackArray() {
        return trackFilter.toArray(new String[trackFilter.size()]);
    }

    public List<String> getTrackList() {
        return trackFilter;
    }
    
    public boolean hasEmptyKeys(){
        return emptyKeys;
    }

    public double[][] getLocationsArray() {
        double[][] locations = new double[locationFilter.size()][2];

        for (int i = 0; i < locationFilter.size(); i++) {
            locations[i][0] = locationFilter.get(i)[0];
            locations[i][1] = locationFilter.get(i)[1];
        }
        return locations;
    }

    public List<Long> getUser2Follow() {
        return user2Follow;
    }
    
     public long[] getUser2FollowArray() {
        final long[] u2fArray=new long[user2Follow.size()];
        
        for(int i=0;i<user2Follow.size();i++){
            u2fArray[i]=user2Follow.get(i);
        }
        return u2fArray;
    }
}
