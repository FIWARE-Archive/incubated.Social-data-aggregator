package com.tilab.ca.sda.ctw.utils;

import java.math.BigDecimal;
import java.util.Collection;


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
}
