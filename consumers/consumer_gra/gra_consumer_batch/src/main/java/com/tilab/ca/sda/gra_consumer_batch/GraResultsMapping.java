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
        spgg.setNumTWMales(sgc.getNumTwMales());
        spgg.setNumRTWMales(sgc.getNumRTwMales());
        spgg.setNumRplyMales(sgc.getNumRplyMales());
        spgg.setNumTWFemales(sgc.getNumTwFemales());
        spgg.setNumRTWFemales(sgc.getNumRTwFemales());
        spgg.setNumRplyFemales(sgc.getNumRplyFemales());
        spgg.setNumTWPages(sgc.getNumTwPages());
        spgg.setNumRTWPages(sgc.getNumRTwPages());
        spgg.setNumRplyPages(sgc.getNumRplyPages());
        spgg.setNumTWUnknown(sgc.getNumTwUnknown());
        spgg.setNumRTWUnknown(sgc.getNumRTwUnknown());
        spgg.setNumRplyUnknown(sgc.getNumRplyUnknown());
  
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
        spgg.setNumTWMales(sgc.getNumTwMales());
        spgg.setNumRTWMales(sgc.getNumRTwMales());
        spgg.setNumRplyMales(sgc.getNumRplyMales());
        spgg.setNumTWFemales(sgc.getNumTwFemales());
        spgg.setNumRTWFemales(sgc.getNumRTwFemales());
        spgg.setNumRplyFemales(sgc.getNumRplyFemales());
        spgg.setNumTWPages(sgc.getNumTwPages());
        spgg.setNumRTWPages(sgc.getNumRTwPages());
        spgg.setNumRplyPages(sgc.getNumRplyPages());
        spgg.setNumTWUnknown(sgc.getNumTwUnknown());
        spgg.setNumRTWUnknown(sgc.getNumRTwUnknown());
        spgg.setNumRplyUnknown(sgc.getNumRplyUnknown());
        
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
        spgh.setNumTWMales(sgc.getNumTwMales());
        spgh.setNumRTWMales(sgc.getNumRTwMales());
        spgh.setNumRplyMales(sgc.getNumRplyMales());
        spgh.setNumTWFemales(sgc.getNumTwFemales());
        spgh.setNumRTWFemales(sgc.getNumRTwFemales());
        spgh.setNumRplyFemales(sgc.getNumRplyFemales());
        spgh.setNumTWPages(sgc.getNumTwPages());
        spgh.setNumRTWPages(sgc.getNumRTwPages());
        spgh.setNumRplyPages(sgc.getNumRplyPages());
        spgh.setNumTWUnknown(sgc.getNumTwUnknown());
        spgh.setNumRTWUnknown(sgc.getNumRTwUnknown());
        spgh.setNumRplyUnknown(sgc.getNumRplyUnknown());
        
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
        spghb.setNumTWMales(sgc.getNumTwMales());
        spghb.setNumRTWMales(sgc.getNumRTwMales());
        spghb.setNumRplyMales(sgc.getNumRplyMales());
        spghb.setNumTWFemales(sgc.getNumTwFemales());
        spghb.setNumRTWFemales(sgc.getNumRTwFemales());
        spghb.setNumRplyFemales(sgc.getNumRplyFemales());
        spghb.setNumTWPages(sgc.getNumTwPages());
        spghb.setNumRTWPages(sgc.getNumRTwPages());
        spghb.setNumRplyPages(sgc.getNumRplyPages());
        spghb.setNumTWUnknown(sgc.getNumTwUnknown());
        spghb.setNumRTWUnknown(sgc.getNumRTwUnknown());
        spghb.setNumRplyUnknown(sgc.getNumRplyUnknown());
        
        return spghb;
    }
    
}
