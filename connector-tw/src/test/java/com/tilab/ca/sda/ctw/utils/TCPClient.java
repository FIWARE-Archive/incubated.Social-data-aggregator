/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.ctw.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPClient {

    Socket clientSocket =null;
    DataOutputStream outToServer =null;
    
    public TCPClient(String serverHost,int serverPort) throws IOException {
         clientSocket=new Socket(serverHost,serverPort);
         outToServer = new DataOutputStream(clientSocket.getOutputStream());
         System.out.println("Connected to "+serverHost+":"+serverPort);
    }
    
    
    
    public void sendToServer(String message){
        try{
            System.out.println("Sending message...");
            outToServer.writeBytes(message + '\n'); 
        }catch(Exception e){
        }
    }
    
    public void closeConnection(){
        try {
            clientSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
