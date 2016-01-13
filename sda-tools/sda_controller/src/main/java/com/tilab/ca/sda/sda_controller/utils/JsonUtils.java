
package com.tilab.ca.sda.sda_controller.utils;

import com.google.gson.Gson;



public class JsonUtils {
    
    private static final Gson gson = new Gson();
    
    public static <T> String serialize(Object obj){
        return gson.toJson(obj);
    }
   
}
