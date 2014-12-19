package com.tilab.ca.sda.utils;

import com.tilab.ca.sda.ctw.utils.Utils;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class UtilsTestCase {

    @Test
    public void notNullOrEmptyTest(){
    
        List<String> nullList=null;
        
        Assert.assertTrue(Utils.isNullOrEmpty(nullList));
        Assert.assertFalse(Utils.isNotNullOrEmpty(nullList));
        
        List<String> emptyList=new LinkedList<>();
        
        Assert.assertTrue(Utils.isNullOrEmpty(emptyList));
        Assert.assertFalse(Utils.isNotNullOrEmpty(emptyList));
        
        List<String> valuedList=new LinkedList<>();
        valuedList.add("1");
        
        Assert.assertTrue(Utils.isNotNullOrEmpty(valuedList));
        Assert.assertFalse(Utils.isNullOrEmpty(valuedList));
    }
    
    @Test
    public void truncateDoubleTest(){
    
        double a=0.123456;
        double b=0.123;
        
        double aTruncate=Utils.truncateDouble(a, 3);
        
        Assert.assertEquals(b,aTruncate,3);
    }
    
}
