package com.tilab.ca.sda.ctw.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import org.apache.log4j.Logger;

public class RoundManager {

    private static final Logger log = Logger.getLogger(RoundManager.class);

    
    /**
     * Round the Date passed as parameter respect to the roundType and
     * granMin criteria
     *
     * @param date Date that has to be rounded
     * @param roundType round type check RoundType class to see the allowed
     * round types
     * @param granMin the granularity expressed in minutes
     * @return
     */
    public static Date roundDate(Date date,int roundType, Integer granMin){
        ZonedDateTime zdt=ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        return getRoundedDateFromZonedDateTime(zdt, roundType, granMin);
    }
    
    /**
     * Convert the ZonedDateTime Object passed as parameter to a Date Object rounded following the 
     * round type criteria 
     * @param zdt ZonedDateTime that has to be rounded and converted to a Date
     * @param roundType round type check RoundType class to see the allowed
     * round types
     * @return a Date object that corresponds to the ZonedDateTime passed as parameter rounded respect
     * to the criteria
     */
    public static Date getSimpleRoundedDateFromZonedDateTime(ZonedDateTime zdt,int roundType){
        return Utils.Time.zonedDateTime2Date(getRoundedDateTime(zdt, roundType, null));
    }
    
    /**
     * Convert the ZonedDateTime Object passed as parameter to a Date Object rounded following the 
     * round type and granMin criteria 
     * @param zdt ZonedDateTime that has to be rounded
     * @param roundType round type check RoundType class to see the allowed
     * round types
     * @param granMin the granularity expressed in minutes
     * @return a Date object that corresponds to the ZonedDateTime passed as parameter rounded respect
     * to the criteria
     */
    public static Date getRoundedDateFromZonedDateTime(ZonedDateTime zdt,int roundType, Integer granMin){
        return Utils.Time.zonedDateTime2Date(getRoundedDateTime(zdt, roundType, granMin));
    }
    
    /**
     * Round the ZonedDateTime passed as parameter respect to the roundType and
     * granMin criteria
     *
     * @param zdt ZonedDateTime that has to be rounded
     * @param roundType round type check RoundType class to see the allowed
     * round types
     * @param granMin the granularity expressed in minutes
     * @return
     */
    public static ZonedDateTime getRoundedDateTime(ZonedDateTime zdt, int roundType, Integer granMin) {
        ZonedDateTime roundedZdt = null;
        switch (roundType) {
            case RoundType.ROUND_TYPE_MIN:
                if (granMin != null && granMin != 0) {
                    roundedZdt = getRoundedDateTimeOnMins(zdt, granMin);
                } else {
                    roundedZdt = getRoundedDateTimeOnMin(zdt);
                }
                break;
            case RoundType.ROUND_TYPE_HOUR:
                roundedZdt = getRoundedDateTimeOnHour(zdt);
                break;
            case RoundType.ROUND_TYPE_DAY:
                roundedZdt = getRoundedDateTimeOnDay(zdt);
                break;
            default:
                throw new IllegalArgumentException("RoundType " + roundType + " not recognized");
        }
        return roundedZdt;
    }
    
    public static final int ONE_MIN=1;
    public static final int MINS_IN_HOUR=60;
    public static final int MINS_IN_DAY=24*MINS_IN_HOUR;
    
    /**
     * Evaluate the granularity from roundType and granMin passed as parameters
     * @param roundType round type check RoundType class to see the allowed
     * round types
     * @param granMin the granularity expressed in minutes
     * @return the correspondent round type in granularity (gran hour -> 60 ,gran day -> 60*24 min)
     */
    public static int getGranMinFromRoundType(int roundType, Integer granMin){
        switch (roundType) {
            case RoundType.ROUND_TYPE_MIN:
                if (granMin != null && granMin != 0) {
                    return granMin;
                } 
                return ONE_MIN;
            case RoundType.ROUND_TYPE_HOUR:
                return MINS_IN_HOUR;
            case RoundType.ROUND_TYPE_DAY:
                return MINS_IN_DAY;
            default:
                throw new IllegalArgumentException("RoundType " + roundType + " not recognized");
        }
    }

    /**
     * Round the ZonedDateTime to the gran related to the minute (e.g granMin=5
     * and zdt=2015-01-10T22:03:03+02 the returned date will be
     * 2015-01-10T22:00:00+02)
     *
     * @param zdt ZonedDateTime that has to be rounded
     * @param granMin minutes on which the date has to be rounded
     * @return the ZonedDateTime passed as parameter rounded to the gran related
     * to the minute
     */
    public static ZonedDateTime getRoundedDateTimeOnMins(ZonedDateTime zdt, int granMin) {
        log.debug("Date Before round is " + zdt.toString());

        long granMillis = granMin * Utils.Time.SECONDS_IN_MINUTE * Utils.Time.MILLISECONDS_IN_SECONDS;
        long zdtMillis = zdt.toInstant().toEpochMilli();
        Instant roundZdtInstant = Instant.ofEpochMilli(zdtMillis - (zdtMillis % granMillis));
        ZonedDateTime roundedZdt = ZonedDateTime.ofInstant(roundZdtInstant, ZoneId.systemDefault());

        log.debug("Date After round is " + roundedZdt.toString());

        return roundedZdt;
    }

    /**
     * Round the ZonedDateTime to the minute
     *
     * @param zdt ZonedDateTime that has to be rounded
     * @return the ZonedDateTime passed as parameter with seconds and
     * milliseconds with 0 value
     */
    public static ZonedDateTime getRoundedDateTimeOnMin(ZonedDateTime zdt) {
        return zdt.withSecond(0).withNano(0);
    }

    /**
     * Round the ZonedDateTime to the hour
     *
     * @param zdt ZonedDateTime that has to be rounded
     * @return the ZonedDateTime passed as parameter with minutes,seconds and
     * milliseconds with 0 value
     */
    public static ZonedDateTime getRoundedDateTimeOnHour(ZonedDateTime zdt) {
        return zdt.withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * Round the ZonedDateTime to the day
     *
     * @param zdt ZonedDateTime that has to be rounded
     * @return the ZonedDateTime passed as parameter with hours,minutes,seconds
     * and milliseconds with 0 value
     */
    public static ZonedDateTime getRoundedDateTimeOnDay(ZonedDateTime zdt) {
        return zdt.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }
}
