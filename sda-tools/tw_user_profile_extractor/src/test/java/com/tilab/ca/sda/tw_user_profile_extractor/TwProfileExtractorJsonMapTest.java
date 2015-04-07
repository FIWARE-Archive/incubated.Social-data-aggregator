/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.tw_user_profile_extractor;

import com.google.gson.Gson;
import com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping.TwProfile;
import com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping.TwProfileEvaluated;
import com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping.TwRawProfile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Administrator
 */
public class TwProfileExtractorJsonMapTest {
    
    /**
     * Test of mapRawJson2TwProfile method, of class TwProfileExtractor.
     */
    @Test
    public void testMapRawJson2TwProfile() {
        System.out.println("mapRawJson2TwProfile");
        
        
        //test complete json with all fields
        String completeRawJson = "{\"id\":1,\"user\":{\"id\":1,\"name\":\"TestUsr1\",\"screenName\":\"test_user_1\",\"profileBackgroundColor\":\"000000\",\"profileTextColor\":\"FFFFFF\",\"description\":\"descr1\",\"location\":\"TestLoc\"}}";
        
        //test with testSetProfile false
        boolean testSetProfiles = false;
        TwProfileEvaluated expResultEvaluated = new TwProfileEvaluated();
        expResultEvaluated.setUserId(1L);
        expResultEvaluated.setPostId(1L);
        expResultEvaluated.setName("TestUsr1");
        expResultEvaluated.setScreenName("test_user_1");
        expResultEvaluated.setProfileBackgroundColor("000000");
        expResultEvaluated.setProfileTextColor("FFFFFF");
        expResultEvaluated.setDescription("descr1");
        expResultEvaluated.setLocation("TestLoc");
        expResultEvaluated.setGender('u');
        
        TwProfile result = TwProfileExtractor.mapRawJson2TwProfile(completeRawJson, testSetProfiles);
        assertTwProfilesEquals(expResultEvaluated, result);
            
        //test with testSetProfile true
        testSetProfiles = true;
        TwRawProfile expResultRaw=new TwRawProfile();
        expResultRaw.setUserId(1L);
        expResultRaw.setPostId(1L);
        expResultRaw.setName("TestUsr1");
        expResultRaw.setScreenName("test_user_1");
        expResultRaw.setProfileBackgroundColor("000000");
        expResultRaw.setProfileTextColor("FFFFFF");
        expResultRaw.setDescription("descr1");
        expResultRaw.setLocation("TestLoc");
        expResultRaw.setGender('u');
        expResultRaw.setDataType(TwRawProfile.TRAINING_TYPE);
        
        result = TwProfileExtractor.mapRawJson2TwProfile(completeRawJson, testSetProfiles);
        assertTwProfilesEquals(expResultRaw, result);
        
        //test json with missing description
        String partialRawJsonNoDesc = "{\"id\":1,\"user\":{\"id\":1,\"name\":\"TestUsr1\",\"screenName\":\"test_user_1\",\"profileBackgroundColor\":\"000000\",\"profileTextColor\":\"FFFFFF\",\"location\":\"TestLoc\"}}";
        testSetProfiles = false;
        expResultEvaluated.setDescription(null);
        result = TwProfileExtractor.mapRawJson2TwProfile(partialRawJsonNoDesc, testSetProfiles);
        assertTwProfilesEquals(expResultEvaluated, result);
        
        //test with testSetProfile true
        testSetProfiles = true;
        expResultRaw.setDescription(null);
        result = TwProfileExtractor.mapRawJson2TwProfile(partialRawJsonNoDesc, testSetProfiles);
        assertTwProfilesEquals(expResultRaw, result);
        
        //test json with missing location
        String partialRawJsonNoLoc = "{\"id\":1,\"user\":{\"id\":1,\"name\":\"TestUsr1\",\"screenName\":\"test_user_1\",\"profileBackgroundColor\":\"000000\",\"profileTextColor\":\"FFFFFF\",\"description\":\"descr1\"}}";
        testSetProfiles = false;
        expResultEvaluated.setDescription("descr1");
        expResultEvaluated.setLocation(null);
        result = TwProfileExtractor.mapRawJson2TwProfile(partialRawJsonNoLoc, testSetProfiles);
        assertTwProfilesEquals(expResultEvaluated, result);
        
        //test with testSetProfile true
        testSetProfiles = true;
        expResultRaw.setDescription("descr1");
        expResultRaw.setLocation(null);
        result = TwProfileExtractor.mapRawJson2TwProfile(partialRawJsonNoLoc, testSetProfiles);
        
        assertTwProfilesEquals(expResultRaw, result);
    }
    
    private void assertTwProfilesEquals(TwProfile expResult,TwProfile result){
        assertEquals(expResult.getClass(), result.getClass());
        assertEquals(expResult.getUserId(), result.getUserId());
        assertEquals(expResult.getPostId(), result.getPostId());
        assertEquals(expResult.getName(), result.getName());
        assertEquals(expResult.getProfileBackgroundColor(), result.getProfileBackgroundColor());
        assertEquals(expResult.getProfileTextColor(), result.getProfileTextColor());
        assertEquals(expResult.getDescription(), result.getDescription());
        assertEquals(expResult.getGender(), result.getGender());
        
        if(expResult instanceof TwRawProfile)
            assertEquals(((TwRawProfile)expResult).getDataType(), ((TwRawProfile)result).getDataType());
            
    }
}
