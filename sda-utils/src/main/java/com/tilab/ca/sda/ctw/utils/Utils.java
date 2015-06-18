package com.tilab.ca.sda.ctw.utils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

public class Utils {

    /**
     * Check if a collection is null or empty
     *
     * @param c the collection that must be checked
     * @return true if the collection is null or empty false otherwise
     */
    public static boolean isNullOrEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    /**
     * Check if a collection is not null or empty
     *
     * @param c the collection that must be checked
     * @return true if the collection is not null nor empty false otherwise
     */
    public static boolean isNotNullOrEmpty(Collection<?> c) {
        return !isNullOrEmpty(c);
    }

    /**
     * Truncate a double to the decimal position passed as parameter
     *
     * @param d the double to truncate
     * @param scale the decimal position at which truncate
     * @return the double truncated at the decimal position passed as parameter
     */
    public static double truncateDouble(double d, int scale) {
        BigDecimal bd = new BigDecimal(d).setScale(scale, BigDecimal.ROUND_DOWN);
        return bd.doubleValue();
    }

    public static class Env {

        /**
         * Retrieve the environment variables corrisponding to the keys passed as parameters (if any) and return the corresponding 
         * path rootPath/confFolder
         * 
         * @param envRootPathName environment variable name that contains the path to the sda root folder
         * @param envConfFolderName environment variable name that contains the path to the specific configuration folder
         * @return the absolute path to the configuration folder
         */
        public static String getConfsPathFromEnv(String envRootPathName, String envConfFolderName) {
            String sdaPath = System.getenv(envRootPathName) != null ? System.getenv(envRootPathName) : System.getProperty(envRootPathName);
            String totTwConsumerConfFolderName = System.getenv(envConfFolderName) != null ? System.getenv(envConfFolderName) : System.getProperty(envConfFolderName);
            if (StringUtils.isBlank(sdaPath) || StringUtils.isBlank(totTwConsumerConfFolderName)) {
                throw new IllegalStateException(String.format("Environment variable %s or %s not setted", envRootPathName, envConfFolderName));
            }
            return sdaPath + File.separator + totTwConsumerConfFolderName;
        }

    }

    public static class Load {
        
        /**
         * Load a properties file located at the local path passed as parameter
         * 
         * @param propsPath the path where is located the property file
         * 
         * @return properties contained into the file
         * @throws Exception 
         */
        public static Properties loadPropertiesFromPath(String propsPath) throws Exception {
            Properties props = new Properties();
            if (propsPath != null) {
                File propsFile = new File(propsPath);
                if (!propsFile.exists()) {
                    throw new IllegalArgumentException("Provided file does not exists on path " + propsPath);
                }
                props.load(new FileInputStream(propsFile));
            }
            return props;
        }

        /**
         * Create an instance of a class passed as parameter (in its string form package.className) that implements the target interface
         * The class must have a constructor that takes as only parameter java Properties (that can be also empty if not needed)
         * 
         * @param interfaceClass the interface class that will be returned from the method 
         * @param implClassStr the location (package.className) of the class that implements the interface
         * @param props the properties that will be passed to the constructor of the class that implements the interface
         * 
         * @return an object of the type of interface class 
         * 
         * @throws Exception if class does not exists or doesn't implement the interface or properties are null 
         */
        public static <T> T getClassInstFromInterface(Class<T> interfaceClass, String implClassStr, Properties props) throws Exception {

            Class<?> implClass = Class.forName(implClassStr);
            if (!interfaceClass.isAssignableFrom(implClass)) {
                throw new IllegalArgumentException(String.format("cannot instantiate impl class %s. Impl class must implements %s interface", implClass, interfaceClass.getName()));
            }

            if (props == null) {
                throw new IllegalArgumentException(String.format("cannot instantiate impl class %s. Properties not provided", implClass));
            }

            return (T) implClass.getConstructor(Properties.class).newInstance(props);
        }

        /**
         * Create an instance of a class passed as parameter (in its string form package.className) that implements the target interface
         * The class must have a constructor that takes as only parameter java Properties (that can be also empty if not needed)
         * 
         * @param interfaceClass the interface class that will be returned from the method 
         * @param implClassStr the location (package.className) of the class that implements the interface
         * @param propsPath the path where is located the property file
         * @return  an object of the type of interface class 
         * @throws Exception Exception if class does not exists or doesn't implement the interface or properties file does not exists
         */
        public static <T> T getClassInstFromInterfaceAndPropsPath(Class<T> interfaceClass, String implClassStr, String propsPath) throws Exception {
            return getClassInstFromInterface(interfaceClass, implClassStr, loadPropertiesFromPath(propsPath));
        }

    }

    public static class Time {

        public static final String ISO_8601_SHORT_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";
        public static final DateTimeFormatter DTF_ISO_8601_SHORT_FORMAT = DateTimeFormatter
                .ofPattern(ISO_8601_SHORT_FORMAT);

        public static final int EXTREME_INCLUDED = 1;
        public static final int EXTREME_EXCLUDED = 2;

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

        /**
         * Convert the Date Object passed as parameter to a ZonedDateTime Object
         *
         * @param date that has to be converted to a ZonedDateTime
         * @return
         */
        public static ZonedDateTime date2ZonedDateTime(Date date) {
            return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }

        /**
         * Check if a zonedDateTime is between two other zonedDateTimes passed
         * as parameters
         *
         * @param from left extreme
         * @param to right extreme
         * @param zdt the ZonedDateTime to compare
         * @param mode Time.EXTREME_EXCLUDED: extreme are excluded
         * Time.EXTREME_INCLUDED: extreme are included
         * @return true if it is contained into the range false otherwise
         */
        public static boolean isBetween(ZonedDateTime from, ZonedDateTime to, ZonedDateTime zdt, int mode) {
            checkIntervalValidity(from, to);
            if(zdt==null)
                throw new IllegalArgumentException("the date to check cannot be null.");
            switch (mode) {
                case EXTREME_EXCLUDED:
                    return zdt.isAfter(from) && zdt.isBefore(to);
                case EXTREME_INCLUDED:
                    return (zdt.isEqual(from) || zdt.isAfter(from)) && (zdt.isEqual(to) || zdt.isBefore(to));

                default:
                    throw new IllegalArgumentException("provided mode is not supported");
            }
        }

        /**
         * Transform a String contains a date in the format ISO-8601 with
         * timezone in short format (+01,-02..) in a zonedDateTime
         *
         * @param str the string containing the date in ISO-8601 with timezone
         * in short format
         * @return the ZonedDateTime
         */
        public static ZonedDateTime fromShortTimeZoneString2ZonedDateTime(String str) {
            if (StringUtils.isBlank(str)) {
                throw new IllegalArgumentException("Input str to parse to date cannot be blank");
            }

            return ZonedDateTime.parse(str, DTF_ISO_8601_SHORT_FORMAT);
        }

        private static void checkIntervalValidity(ZonedDateTime from, ZonedDateTime to) {
            if (from == null || to == null) {
                throw new IllegalArgumentException("from and to cannot be null");
            }
            if (from.isAfter(to)) {
                throw new IllegalArgumentException("Invalid range. From cannot be after to");
            }
        }

    }
}
