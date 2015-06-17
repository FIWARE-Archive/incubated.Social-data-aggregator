package com.tilab.ca.sda.gra_consumer_batch;

import com.tilab.ca.sda.ctw.utils.RoundManager;
import com.tilab.ca.sda.ctw.utils.Utils;
import com.tilab.ca.sda.gra_consumer_dao.data.StatsPreGenderGeo;
import com.tilab.ca.sda.gra_consumer_dao.data.StatsPreGenderGeoBound;
import com.tilab.ca.sda.gra_consumer_dao.data.StatsPreGenderHt;
import com.tilab.ca.sda.gra_consumer_dao.data.StatsPreGenderHtBound;
import com.tilab.ca.sda.gra_consumer_dao.data.TwGenderProfile;
import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.gra_core.StatsGenderCount;
import com.tilab.ca.sda.sda.model.keys.DateHtKey;
import com.tilab.ca.sda.sda.model.keys.GeoLocTruncKey;
import com.tilab.ca.sda.sda.model.keys.GeoLocTruncTimeKey;
import java.time.ZonedDateTime;


public class GraResultsMapping {
    
     /**
     * 
     * @param pg
     * @return 
     */
    public static TwGenderProfile fromProfileGender2TwGenderProfile(ProfileGender pg){
        return new TwGenderProfile(pg.getTwProfile().getUid(), 
                                   pg.getTwProfile().getScreenName(), 
                                   pg.getGender().toChar());
        
    }
    
    /**
     * 
     * @param gltk
     * @param sgc
     * @param roundType
     * @param granMin
     * @return 
     */
    public static StatsPreGenderGeo fromStatsGenderCountToStatsPreGenderGeo(GeoLocTruncTimeKey gltk,StatsGenderCount sgc,int roundType, Integer granMin){
        StatsPreGenderGeo spgg=new StatsPreGenderGeo();
        spgg.setCreatedAt(gltk.getDate());
        spgg.setLatTrunc(gltk.getGeoLocTruncKey().getLatTrunc());
        spgg.setLongTrunc(gltk.getGeoLocTruncKey().getLongTrunc());
        spgg.setGran(RoundManager.getGranMinFromRoundType(roundType, granMin));
        spgg.setNumFemales(sgc.getNumTwMales());
        spgg.setNumMales(sgc.getNumTwFemales());
        spgg.setNumPages(sgc.getNumTwPages());
        spgg.setNumUndefined(sgc.getNumTwUndefined());
        
        return spgg;
    }
    
    /**
     * 
     * @param gltk
     * @param sgc
     * @param from
     * @param to
     * @return 
     */
    public static StatsPreGenderGeoBound fromStatsGenderCountToStatsPreGenderGeoBound(GeoLocTruncKey gltk,StatsGenderCount sgc,ZonedDateTime from,ZonedDateTime to){
        StatsPreGenderGeoBound spgg=new StatsPreGenderGeoBound();
        spgg.setFrom(Utils.Time.zonedDateTime2Date(from));
        spgg.setTo(Utils.Time.zonedDateTime2Date(to));
        spgg.setLatTrunc(gltk.getLatTrunc());
        spgg.setLongTrunc(gltk.getLongTrunc());
        spgg.setNumFemales(sgc.getNumTwMales());
        spgg.setNumMales(sgc.getNumTwFemales());
        spgg.setNumPages(sgc.getNumTwPages());
        spgg.setNumUndefined(sgc.getNumTwUndefined());
        
        return spgg;
    }
    
    /**
     * 
     * @param hk
     * @param sgc
     * @param roundType
     * @param granMin
     * @return 
     */
    public static StatsPreGenderHt fromStatsGenderCountToStatsPreGenderHt(DateHtKey hk,StatsGenderCount sgc,int roundType, Integer granMin){
        StatsPreGenderHt spgh=new StatsPreGenderHt();
        spgh.setCreatedAt(hk.getDate());
        spgh.setHt(hk.getHt());
        spgh.setGran(RoundManager.getGranMinFromRoundType(roundType, granMin));
        spgh.setNumFemales(sgc.getNumTwMales());
        spgh.setNumMales(sgc.getNumTwFemales());
        spgh.setNumPages(sgc.getNumTwPages());
        spgh.setNumUndefined(sgc.getNumTwUndefined());
        
        return spgh;
    }
    
    /**
     * 
     * @param ht
     * @param sgc
     * @param from
     * @param to
     * @return 
     */
    public static StatsPreGenderHtBound fromStatsGenderCountToStatsPreGenderHtBound(String ht,StatsGenderCount sgc,ZonedDateTime from,ZonedDateTime to){
        StatsPreGenderHtBound spghb=new StatsPreGenderHtBound();
        spghb.setFrom(Utils.Time.zonedDateTime2Date(from));
        spghb.setTo(Utils.Time.zonedDateTime2Date(to));
        spghb.setHt(ht);
        spghb.setNumFemales(sgc.getNumTwMales());
        spghb.setNumMales(sgc.getNumTwFemales());
        spghb.setNumPages(sgc.getNumTwPages());
        spghb.setNumUndefined(sgc.getNumTwUndefined());
        
        return spghb;
    }
    
}
