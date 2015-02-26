package com.tilab.ca.sda.ctw.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class JsonUtils {
    
    public static final String DATE_ISO_8601_FORMAT="yyyy-MM-dd'T'HH:mm:ssX";
    
    /**
     * Serialize a java object to a json string
     * @param obj the object to serialize
     * @return a string containing the json representation of the object
     */
    public static String serialize(Object obj) {
        Gson gson = new GsonBuilder()
                .setDateFormat(DATE_ISO_8601_FORMAT)
                .create();
        return gson.toJson(obj);
    }
    
    /**
     * Deserialize a json string into the corrispondenting object
     * @param json the json string to deserialize
     * @param cls the class from which create the object
     * @return an instantiation object of the target class valorized with the content of the json string
     */
    public static <T> T deserialize(String json,Class<T> cls){
         Gson gson = new GsonBuilder()
                .setDateFormat(DATE_ISO_8601_FORMAT)
                .create();
         return gson.fromJson(json, cls);
    }
}
