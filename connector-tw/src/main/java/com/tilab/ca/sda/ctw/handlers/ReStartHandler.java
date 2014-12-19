package com.tilab.ca.sda.ctw.handlers;

import com.tilab.ca.sda.ctw.Constants;
import com.tilab.ca.sda.ctw.utils.stream.SparkStreamingManager;
import java.io.IOException;
import java.util.concurrent.Executors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;


public class ReStartHandler extends AbstractHandler {

    private static final Logger log = Logger.getLogger(ReStartHandler.class);
    private final SparkStreamingManager sparkStreamingManager;
    
    
    public ReStartHandler(SparkStreamingManager sparkStreamingManager){
        this.sparkStreamingManager = sparkStreamingManager;
    }

    
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {

        log.info(String.format("[%s] Received a call to restart", Constants.TW_RECEIVER_LOG_TAG));
        
        response.setContentType("text/plain");
        baseRequest.setHandled(true);

        try {
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    sparkStreamingManager.restartSparkStreaming();
                } catch (Exception e) {
                    log.error(String.format("[%s] Error on restart!",
                            Constants.TW_RECEIVER_LOG_TAG), e);
                }
            });

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("Done");
        } catch (IOException ex) {
            log.error(String.format("[%s] Exception during app initialization!",
                    Constants.TW_RECEIVER_LOG_TAG), ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }
}
