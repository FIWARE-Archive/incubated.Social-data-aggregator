package com.tilab.ca.sda.utils;

import com.tilab.ca.sda.ctw.utils.Utils;
import com.tilab.ca.sda.utils.mocks.TstInterface;
import com.tilab.ca.sda.utils.mocks.TstWrongInterface;
import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;

public class UtilsTestCase {

    @Test
    public void notNullOrEmptyTest() {

        List<String> nullList = null;

        Assert.assertTrue(Utils.isNullOrEmpty(nullList));
        Assert.assertFalse(Utils.isNotNullOrEmpty(nullList));

        List<String> emptyList = new LinkedList<>();

        Assert.assertTrue(Utils.isNullOrEmpty(emptyList));
        Assert.assertFalse(Utils.isNotNullOrEmpty(emptyList));

        List<String> valuedList = new LinkedList<>();
        valuedList.add("1");

        Assert.assertTrue(Utils.isNotNullOrEmpty(valuedList));
        Assert.assertFalse(Utils.isNullOrEmpty(valuedList));
    }

    @Test
    public void truncateDoubleTest() {

        double a = 0.123456;
        double b = 0.123;

        double aTruncate = Utils.truncateDouble(a, 3);

        Assert.assertEquals(b, aTruncate, 3);
    }

    @Test(expected = IllegalStateException.class)
    public void envTestNullBothProps() {

        String rootPathProp = "sdahomePath";
        String folderProp = "confPath";
        Utils.Env.getConfsPathFromEnv(rootPathProp, folderProp);
    }

    @Test(expected = IllegalStateException.class)
    public void envTestNullOneProp() {

        String rootPathProp = "sdahomePath";
        String folderProp = "confPath";

        System.setProperty(folderProp, "conf");
        Utils.Env.getConfsPathFromEnv(rootPathProp, folderProp);
    }

    @Test
    public void envTest() {

        String rootPathProp = "sdahomePath";
        String folderProp = "confPath";

        System.setProperty(rootPathProp, "#home#usr1#sda".replace("#", File.separator));
        System.setProperty(folderProp, "conf");

        Assert.assertEquals("#home#usr1#sda#conf".replace("#", File.separator), Utils.Env.getConfsPathFromEnv(rootPathProp, folderProp));

        System.clearProperty(rootPathProp);
        System.clearProperty(folderProp);

    }

    @Test
    public void loadPropsTest() throws Exception {
        String workingDir = System.getProperty("user.dir");
        String filePath = String.format("%s#src#test#resources#testProps.properties",
                workingDir).replace("#", File.separator);

        Properties props = Utils.Load.loadPropertiesFromPath(filePath);
        Assert.assertEquals("val1", props.getProperty("key1"));
        Assert.assertEquals("val2", props.getProperty("key2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void loadPropsNoFileTest() throws Exception {
        String workingDir = System.getProperty("user.dir");
        String filePath = String.format("%s#src#test#resources#data#testProps.properties",
                workingDir).replace("#", File.separator);
        Utils.Load.loadPropertiesFromPath(filePath);
    }
    
    @Test
    public void getClassFromInterfaceSimpleTest() throws Exception {
        Properties props=new Properties();
        props.put("val1", "1");
        props.put("val2", "2");
        TstInterface testObj=Utils.Load.getClassInstFromInterface(TstInterface.class, "com.tilab.ca.sda.utils.mocks.TstImpl", props);
        Assert.assertEquals(1, testObj.val1());
        Assert.assertEquals(2, testObj.val2());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getClassFromInterfaceWrongInterfaceTest() throws Exception {
        Properties props=new Properties();
        props.put("val1", "1");
        props.put("val2", "2");
        Utils.Load.getClassInstFromInterface(TstWrongInterface.class, "com.tilab.ca.sda.utils.mocks.TstImpl", props);
    }
    
    @Test(expected = ClassNotFoundException.class)
    public void getClassFromInterfaceInexistentClassTest() throws Exception {
        Properties props=new Properties();
        props.put("val1", "1");
        props.put("val2", "2");
        Utils.Load.getClassInstFromInterface(TstInterface.class, "com.tilab.ca.sda.utils.mocks.TsImplNonExistent", props);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getClassFromInterfaceNullPropsTest() throws Exception {
        Utils.Load.getClassInstFromInterface(TstInterface.class, "com.tilab.ca.sda.utils.mocks.TstImpl", null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void fromShortTimeZoneString2ZonedDateTimeNullStrTest(){
        Utils.Time.fromShortTimeZoneString2ZonedDateTime(null);
    }
    
    @Test(expected = DateTimeParseException.class)
    public void fromShortTimeZoneString2ZonedDateTimeWrongDateFormatTest(){
        Utils.Time.fromShortTimeZoneString2ZonedDateTime("2014-05-12 12:35:00.000+01");
    }
    
    @Test
    public void fromShortTimeZoneString2ZonedDateTimeTest() throws Exception{
        ZonedDateTime zdt=Utils.Time.fromShortTimeZoneString2ZonedDateTime("2014-05-12T12:35:00+01");
        Assert.assertEquals(2014,zdt.getYear());
        Assert.assertEquals(5,zdt.getMonthValue());
        Assert.assertEquals(12,zdt.getDayOfMonth());
        Assert.assertEquals(12,zdt.getHour());
        Assert.assertEquals(35,zdt.getMinute());
        Assert.assertEquals(0,zdt.getSecond());
        Assert.assertEquals(3600,zdt.getOffset().getTotalSeconds());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void isBetweenNullFromToTest() throws Exception {
        ZonedDateTime c=ZonedDateTime.now();
        Utils.Time.isBetween(null, null, c, Utils.Time.EXTREME_EXCLUDED);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void isBetweenNullFromTest() throws Exception {
        ZonedDateTime to=ZonedDateTime.now();
        ZonedDateTime c=ZonedDateTime.now();
        Utils.Time.isBetween(null, to, c, Utils.Time.EXTREME_EXCLUDED);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void isBetweenNullToTest() throws Exception {
        ZonedDateTime from=ZonedDateTime.now();
        ZonedDateTime c=ZonedDateTime.now();
        Utils.Time.isBetween(from, null, c, Utils.Time.EXTREME_EXCLUDED);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void isBetweenNullDateTest() throws Exception {
        ZonedDateTime from=ZonedDateTime.now();
        ZonedDateTime to=ZonedDateTime.now().plusMinutes(10);
        Utils.Time.isBetween(from, to, null, Utils.Time.EXTREME_EXCLUDED);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void isBetweenFromAfterToTest() throws Exception {
        ZonedDateTime from=ZonedDateTime.now().plusMinutes(10);
        ZonedDateTime to=ZonedDateTime.now().plusMinutes(5);
        ZonedDateTime testData=ZonedDateTime.now();
        Utils.Time.isBetween(from, to, testData, Utils.Time.EXTREME_EXCLUDED);
    }
    
    @Test
    public void isBetweenTestDateInRangeTest() throws Exception {
        ZonedDateTime from=ZonedDateTime.now().minusMinutes(10);
        ZonedDateTime testData=ZonedDateTime.now();
        ZonedDateTime to=ZonedDateTime.now().plusMinutes(5);
        
        Assert.assertTrue(Utils.Time.isBetween(from, to, testData, Utils.Time.EXTREME_EXCLUDED));
    }
    
    @Test
    public void isBetweenTestDateExtremeFromExtremeExcludedTest() throws Exception {
        ZonedDateTime from=ZonedDateTime.now().minusMinutes(10).withNano(0);
        ZonedDateTime testData=ZonedDateTime.now().minusMinutes(10).withNano(0);
        ZonedDateTime to=ZonedDateTime.now().plusMinutes(5);
        
        Assert.assertFalse(Utils.Time.isBetween(from, to, testData, Utils.Time.EXTREME_EXCLUDED));
    }
    
    @Test
    public void isBetweenTestDateExtremeFromExtremeIncludedTest() throws Exception {
        ZonedDateTime from=ZonedDateTime.now().minusMinutes(10).withNano(0);
        ZonedDateTime testData=ZonedDateTime.now().minusMinutes(10).withNano(0);
        ZonedDateTime to=ZonedDateTime.now().plusMinutes(5);
        
        Assert.assertTrue(Utils.Time.isBetween(from, to, testData, Utils.Time.EXTREME_INCLUDED));
    }
    
    @Test
    public void isBetweenTestDateExtremeToExtremeExcludedTest() throws Exception {
        ZonedDateTime from=ZonedDateTime.now().minusMinutes(10).withNano(0);
        ZonedDateTime testData=ZonedDateTime.now().plusMinutes(5).withNano(0);
        ZonedDateTime to=ZonedDateTime.now().plusMinutes(5).withNano(0);
        
        Assert.assertFalse(Utils.Time.isBetween(from, to, testData, Utils.Time.EXTREME_EXCLUDED));
    }
    
    @Test
    public void isBetweenTestDateExtremeToExtremeIncludedTest() throws Exception {
        ZonedDateTime from=ZonedDateTime.now().minusMinutes(10).withNano(0);
        ZonedDateTime testData=ZonedDateTime.now().plusMinutes(5).withNano(0);
        ZonedDateTime to=ZonedDateTime.now().plusMinutes(5).withNano(0);
        
        Assert.assertTrue(Utils.Time.isBetween(from, to, testData, Utils.Time.EXTREME_INCLUDED));
    }
    
    @Test
    public void isBetweenTestDateOutOfRangeAfteToTest() throws Exception {
        ZonedDateTime from=ZonedDateTime.now().minusMinutes(10);
        ZonedDateTime testData=ZonedDateTime.now().plusMinutes(10);
        ZonedDateTime to=ZonedDateTime.now().plusMinutes(5);
        
        Assert.assertFalse(Utils.Time.isBetween(from, to, testData, Utils.Time.EXTREME_INCLUDED));
    }
    
    @Test
    public void isBetweenTestDateOutOfRangeBeforeFromTest() throws Exception {
        ZonedDateTime from=ZonedDateTime.now().minusMinutes(10);
        ZonedDateTime testData=ZonedDateTime.now().minusMinutes(15);
        ZonedDateTime to=ZonedDateTime.now().plusMinutes(5);
        
        Assert.assertFalse(Utils.Time.isBetween(from, to, testData, Utils.Time.EXTREME_INCLUDED));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void isBetweenNotSupportedModeTest() throws Exception {
        ZonedDateTime from=ZonedDateTime.now().minusMinutes(10);
        ZonedDateTime testData=ZonedDateTime.now().minusMinutes(15);
        ZonedDateTime to=ZonedDateTime.now().plusMinutes(5);
        
        Utils.Time.isBetween(from, to, testData, 5);
    }
}
