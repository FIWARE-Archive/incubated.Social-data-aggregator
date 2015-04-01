/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.tw_user_profile_extractor;

import com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping.TwProfile;
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
        String rawJson = "";
        boolean testSetProfiles = false;
        TwProfile expResult = null;
        TwProfile result = TwProfileExtractor.mapRawJson2TwProfile(rawJson, testSetProfiles);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
