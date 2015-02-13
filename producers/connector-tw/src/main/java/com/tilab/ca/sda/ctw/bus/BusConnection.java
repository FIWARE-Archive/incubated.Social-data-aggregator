/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.ctw.bus;

import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public interface BusConnection<K,V> extends Serializable{
    
   
    /**
     * send the last message setted on the provided topic
     * @param providedTopic
     * @param msg
     */
    public void send(String providedTopic,V msg);
    
    /**
     * Send the message on the provided topic with the provided key
     * @param providedTopic
     * @param msg
     * @param providedKey 
     */
    public void send(String providedTopic,V msg,K providedKey);
    
    /**
     * Close the connection to the bus and eventually clean other allocate objects
     */
    public void dispose();
}
