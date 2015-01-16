/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.ctw.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author Administrator
 */
public class JsonUtils {
    
    public static String serialize(Object obj) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
                .create();
        return gson.toJson(obj);
    }
}
