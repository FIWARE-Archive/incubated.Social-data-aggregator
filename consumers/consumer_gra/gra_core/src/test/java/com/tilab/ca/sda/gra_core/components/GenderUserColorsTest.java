/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.gra_core.components;

import com.tilab.ca.sda.gra_core.GenderTypes;
import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.sda.model.TwUserProfile;
import org.apache.spark.api.java.JavaRDD;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dino
 */
public class GenderUserColorsTest {
    
    public GenderUserColorsTest() {
    }
    
    /**
     * Test of getGendersFromTwProfiles method, of class GenderUserColors.
    
    @Test
    public void testGetGendersFromTwProfiles() {
        System.out.println("getGendersFromTwProfiles");
        JavaRDD<ProfileGender> profilesRDD = null;
        GenderUserColors instance = null;
        JavaRDD<ProfileGender> expResult = null;
        JavaRDD<ProfileGender> result = instance.getGendersFromTwProfiles(profilesRDD);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
 */
    /**
     * Test of getGenderFromProfileColours method, of class GenderUserColors.
     */
    @Test
    public void testGetGenderFromProfileColours() {
        System.out.println("getGenderFromProfileColours");
        /*
        TwUserProfile twUserProfile = null;
        GenderUserColors instance = null;
        GenderTypes expResult = null;
        GenderTypes result = instance.getGenderFromProfileColours(twUserProfile);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");*/
    }
    
}
