/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.ctw.utils;

import com.tilab.ca.sda.ctw.mocks.ExpectedOutputHandlerMsgBusImpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPServer {
    
    private ServerSocket welcomeSocket = null; 
    private ExpectedOutputHandlerMsgBusImpl<String,String> eoh;
    private Thread t;
    
    public TCPServer(int port,ExpectedOutputHandlerMsgBusImpl<String,String> eoh) throws Exception{
       welcomeSocket = new ServerSocket(port); 
       this.eoh=eoh;
    }
    
    public void start(){
        t=new Thread( () -> {
            System.out.println("socket listening on port "+welcomeSocket.getLocalPort());
            while(true){
                try {
                    Socket connectionSocket = welcomeSocket.accept();
                    new Thread(()-> handleNewClient(connectionSocket)).start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        t.start();
    }
    
    public void handleNewClient(Socket connectionSocket){
        try {
            BufferedReader inFromClient=new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            String message=null;
            while((message=inFromClient.readLine())!=null){
                System.out.println("Received: " + message);
                eoh.addOutputItem(message);
            }
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void stop() throws IOException{
        System.out.println("Shutdown server..");
        t.interrupt();
        welcomeSocket.close();
        welcomeSocket=null;
    }
}
