package com.tilab.ca.sda.ctw.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

public class Utils {

    public static boolean isNullOrEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static boolean isNotNullOrEmpty(Collection<?> c) {
        return !isNullOrEmpty(c);
    }

    public static double truncateDouble(double d, int scale) {
        BigDecimal bd = new BigDecimal(d).setScale(scale, BigDecimal.ROUND_DOWN);
        return bd.doubleValue();
    }
    
    public static class Load{
        
        public static Properties loadPropertiesFromPath(String propsPath) throws Exception{
            Properties props=new Properties();
            if(propsPath!=null){
                File propsFile=new File(propsPath);
                if(!propsFile.exists())
                    throw new IllegalArgumentException("Provided file does not exists on path "+propsPath);
                props.load(new FileInputStream(propsFile));
            }
            return props;
        }
        
        public static <T> T getClassInstFromInterface(Class<T> interfaceClass,String implClassStr,Properties props) throws Exception{
        
            Class<?> implClass = Class.forName(implClassStr);
            if(!interfaceClass.isAssignableFrom(implClass))
                throw new IllegalArgumentException(String.format("cannot instantiate impl class %s. Impl class must implements %s interface",implClass,interfaceClass.getName()));
 
            return (T)implClass.getConstructor(Properties.class).newInstance(props);
        } 
        
        public static <T> T getClassInstFromInterfaceAndPropsPath(Class<T> interfaceClass,String implClassStr,String propsPath) throws Exception{
            return getClassInstFromInterface(interfaceClass, implClassStr, loadPropertiesFromPath(propsPath));
        }
   
    }

    public static class Env {

        public static String getConfsPathFromEnv(String envRootPathName,String envConfFolderName) {
            String sdaPath = System.getenv(envRootPathName) != null ? System.getenv(envRootPathName) : System.getProperty(envRootPathName);
            String totTwConsumerConfFolderName = System.getenv(envConfFolderName) != null ? System.getenv(envConfFolderName) : System.getProperty(envConfFolderName);
            if (StringUtils.isBlank(sdaPath) || StringUtils.isBlank(totTwConsumerConfFolderName)) {
                throw new IllegalStateException(String.format("Environment variable %s or %s not setted", envRootPathName, envConfFolderName));
            }
            return sdaPath + File.separator + totTwConsumerConfFolderName;
        }

    }

    public static class Time {

        public static final String ISO_8601_SHORT_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";
        public static final DateTimeFormatter DTF_ISO_8601_SHORT_FORMAT = DateTimeFormatter
                .ofPattern(ISO_8601_SHORT_FORMAT);
        
        public static final int EXTREME_INCLUDED=1;
        public static final int EXTREME_EXCLUDED=2;
        
        public static final int SECONDS_IN_MINUTE = 60;
        public static final int MILLISECONDS_IN_SECONDS = 1000;

        /**
         * Convert the ZonedDateTime Object passed as parameter to a Date Object
         *
         * @param zdt ZonedDateTime that has to be converted to a Date
         * @return
         */
        public static Date zonedDateTime2Date(ZonedDateTime zdt) {
            Date d = Date.from(zdt.toInstant());
            return d;
        }
        
        public static ZonedDateTime date2ZonedDateTime(Date date){
            return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }
        
        public static boolean isBetween(ZonedDateTime from,ZonedDateTime to,ZonedDateTime zdt,int mode){
            switch(mode){
                case EXTREME_EXCLUDED:
                    return zdt.isAfter(from) && zdt.isBefore(to); 
                case EXTREME_INCLUDED:
                    return (zdt.isEqual(from)||zdt.isAfter(from)) && (zdt.isEqual(to)||zdt.isBefore(to)); 
                
                default: throw new IllegalArgumentException("provided mode is not supported");
            }
        }
        
        public static ZonedDateTime fromShortTimeZoneString2ZonedDateTime(String str){
			return ZonedDateTime.parse(str,DTF_ISO_8601_SHORT_FORMAT);
        }

    }
}
