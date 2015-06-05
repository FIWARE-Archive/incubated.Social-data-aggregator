package com.tilab.ca.sda.gra_core.components;

import org.junit.Test;
import static org.junit.Assert.*;


public class GenderNameSNTest {
    /**
     * Tests of cleanName method, of class GenderNameSN.
     */
    @Test
    public void testCleanName() {
        System.out.println("testCleanName");
        String name = "FirstTest";
        String expResult = "first test";
        String result = GenderNameSN.cleanName(name);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testCleanNameManyCapitalLetters() {
        System.out.println("testCleanNameManyCapitalLetters");
        String name = "SecondTestIsMoreComplex";
        String expResult = "second test is more complex";
        String result = GenderNameSN.cleanName(name);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testCleanNameNoAlphabeticalLetters() {
        System.out.println("testCleanNameNoAlphabeticalLetters");
        String name = "User123TestName?? 87!! __and__";
        String expResult = "user test name and";
        String result = GenderNameSN.cleanName(name);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testAllUppercase() {
        System.out.println("testAllUppercase");
        String name = "UPPERCASENAME";
        String expResult = "uppercasename";
        String result = GenderNameSN.cleanName(name);
        assertEquals(expResult, result);
    }
    
}
