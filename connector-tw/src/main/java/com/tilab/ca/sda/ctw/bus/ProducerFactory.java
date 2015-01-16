/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.ctw.bus;

import java.io.Serializable;


public interface ProducerFactory<K,V> extends Serializable{
     
    public BusConnection<K,V> newInstance();
}
