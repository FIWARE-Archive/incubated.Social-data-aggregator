/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.gra_core.utils;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dino
 */
public class ColourUtilsTest {
    
    
    /**
     * Test of hex2Rgb method, of class ColourUtils.
     */
    @Test
    public void testHex2Rgb() {
        System.out.println("hex2Rgb");
        String colorHexStr = "CCFF33";
        Color expResult = new Color(204, 255, 51);
        Color result = ColourUtils.hex2Rgb(colorHexStr);
        assertEquals(expResult, result);
    }

    /**
     * Test of generatePaletteRGB method, of class ColourUtils.
    */
    @Test
    public void testGeneratePaletteRGB() {
        System.out.println("generatePaletteRGB");
        int numBits = 3;
        Map<Color, Integer> expResult = new HashMap<>();
        expResult.put(new Color(0,0,0), 1);
        expResult.put(new Color(0,0,1), 2);
        expResult.put(new Color(0,1,0), 3);
        expResult.put(new Color(0,1,1), 4);
        expResult.put(new Color(1,0,0), 5);
        expResult.put(new Color(1,0,1), 6);
        expResult.put(new Color(1,1,0), 7);
        expResult.put(new Color(1,1,1), 8);
        
        Map<Color, Integer> result = ColourUtils.generatePaletteRGB(numBits);
        assertEquals(expResult, result);
    }

    /**
     * Test of changeRGBbitDepth method, of class ColourUtils.
    */
    @Test
    public void testChangeRGBbitDepth() {
        System.out.println("changeRGBbitDepth");
        Color source = new Color(204, 255, 51);
        int numBitsIn = 24;
        int numBitsOut = 9;
        Color expResult = new Color(5, 7, 1);
        Color result = ColourUtils.changeRGBbitDepth(source, numBitsIn, numBitsOut);
        assertEquals(expResult, result);
    }
    
}
