/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.ctw.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;

/**
 *
 * @author Administrator
 */
public class TestUtils {
 
    public static void assertMatches(String regexpr, String val) {
        Pattern pattern = Pattern.compile(regexpr);
        Matcher matcher = pattern.matcher(val);
        Assert.assertTrue(matcher.matches());
    }
}
