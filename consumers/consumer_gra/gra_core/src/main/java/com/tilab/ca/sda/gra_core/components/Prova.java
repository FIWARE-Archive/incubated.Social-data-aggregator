/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.gra_core.components;

import com.tilab.ca.sda.gra_core.GenderTypes;
import com.tilab.ca.sda.gra_core.ProfileGender;
import com.tilab.ca.sda.sda.model.TwUserProfile;
import java.io.Serializable;
import org.apache.spark.api.java.JavaRDD;

/**
 *
 * @author dino
 */
public class Prova implements Serializable{
 
    public GenderTypes getGenderFromNameScreenName(String name,String screenName){
        return GenderTypes.MALE;
    }
    
    
}
