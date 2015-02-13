package com.tilab.ca.sda.ctw.utils;

import com.tilab.ca.sda.ctw.mocks.ExpectedOutputHandlerMsgBusImpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TCPServer {

    private ServerSocket welcomeSocket = null;
    private static ExpectedOutputHandlerMsgBusImpl<String, String> eoh;
    private final ExecutorService clientConnectionThreadExecutor;
    private final ExecutorService listenSocketThreadExecutor;
    private boolean stopped = true;

    public TCPServer(int port, ExpectedOutputHandlerMsgBusImpl<String, String> eoh) throws Exception {
        welcomeSocket = new ServerSocket(port);
        this.eoh = eoh;
        clientConnectionThreadExecutor = Executors.newCachedThreadPool();
        listenSocketThreadExecutor = Executors.newSingleThreadExecutor();
    }

    public void start() {
        stopped = false;
        listenSocketThreadExecutor.submit((new Runnable() {

            @Override
            public void run() {
                System.out.println("socket listening on port " + welcomeSocket.getLocalPort());
                while (true) {
                    try {
                        Socket connectionSocket = welcomeSocket.accept();
                        clientConnectionThreadExecutor.submit(() -> handleNewClient(connectionSocket));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }));

    }

    public void handleNewClient(Socket connectionSocket) {
        try {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            String message = null;
            while ((message = inFromClient.readLine()) != null) {
                System.out.println("Received: " + message);
                eoh.addOutputItem(message);
            }
            connectionSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void stop() throws Exception {
        System.out.println("Shutdown server..");
        clientConnectionThreadExecutor.shutdown();
        clientConnectionThreadExecutor.awaitTermination(1, TimeUnit.SECONDS);
        listenSocketThreadExecutor.shutdown();
        listenSocketThreadExecutor.awaitTermination(1, TimeUnit.SECONDS);
        //close server socket
        if (welcomeSocket != null && !welcomeSocket.isClosed()) {
            welcomeSocket.close();
            welcomeSocket = null;
        }
        stopped = true;
    }

    public boolean isStopped() {
        return stopped;
    }
}
