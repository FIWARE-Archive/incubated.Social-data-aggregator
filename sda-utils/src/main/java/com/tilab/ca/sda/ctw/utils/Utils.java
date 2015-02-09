package com.tilab.ca.sda.ctw.utils;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;

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
