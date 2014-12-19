package com.tilab.ca.sda.ctw.utils;

import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;

public class JettyServerManager {
    
    private static final Logger log = Logger.getLogger(JettyServerManager.class);
    private static final int DEFAULT_PORT=8888;
    
    private List<Handler> handlerList = null;
    private Server server = null;
    private int port = DEFAULT_PORT;
    
    
    public static JettyServerManager newInstance(){
        return new JettyServerManager();
    }

    public void startServerOnNewThread() {
        Thread t = new Thread(() -> {
            log.info("Starting jetty server on port " + port);
            server = new Server(port);
            try {
                HandlerList handlers = new HandlerList();
                handlers.setHandlers(handlerList.toArray(new Handler[handlerList.size()]));
                server.setHandler(handlers);
                
                server.start();
                server.join();
            } catch (Exception e) {
                log.error("Error on starting jetty server", e);
            }
        });
        t.start();
    }

    public JettyServerManager addContextHandler(String contextPath, Handler handler) {
        ContextHandler context = new ContextHandler();
        context.setContextPath(contextPath);
        context.setResourceBase(".");
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        context.setHandler(handler);
        if (handlerList == null) {
            handlerList = new LinkedList<>();
        }
        handlerList.add(context);
        return this;
    }

    public JettyServerManager port(int port) {
        this.port = port;
        return this;
    }
}
