package com.tilab.ca.sda.ctw.handlers;

import com.tilab.ca.sda.ctw.Constants;
import com.tilab.ca.sda.ctw.TwStreamConnectorMain;
import com.tilab.ca.sda.ctw.TwStreamConnectorProperties;
import com.tilab.ca.sda.ctw.TwitterStreamConnector;
import com.tilab.ca.sda.ctw.dao.TwStatsDao;
import com.tilab.ca.sda.ctw.utils.stream.SparkStreamingManager;
import java.io.IOException;
import java.util.concurrent.Executors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class StartHandler extends AbstractHandler {

    private static final Logger log = Logger.getLogger(StartHandler.class);
    private final SparkStreamingManager sparkStreamingManager;
    private final TwStreamConnectorProperties twProps;
    private final TwStatsDao twStatDao;
    //private final BusConnectionPool<String, String> connectionPool;

    public StartHandler(SparkStreamingManager sparkStreamingManager,
            TwStreamConnectorProperties twProps,
            TwStatsDao twStatDao) {
        //BusConnectionPool<String, String> connectionPool
        this.sparkStreamingManager = sparkStreamingManager;
        this.twProps = twProps;
        this.twStatDao = twStatDao;
//        this.connectionPool = connectionPool;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {

        log.info(String.format("[%s] Received a call to start", Constants.TW_RECEIVER_LOG_TAG));
        response.setContentType("text/plain");

        baseRequest.setHandled(true);

        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("Done");
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    sparkStreamingManager.startSparkStream((jssc) -> {
                        TwitterStreamConnector tsc=new TwitterStreamConnector(twProps, twStatDao);
                        if (StringUtils.isNotBlank(twProps.brokersList())){
                            log.info(String.format("[%s] Bus enabled. Data will be sent on it", Constants.SDA_TW_CONNECTOR_LOG_TAG));
                            tsc=tsc.withProducerFactory(TwStreamConnectorMain.createProducerFactory(twProps))
                                   .withProducerPoolConf(TwStreamConnectorMain.createBusConnectionPoolConfiguration(twProps));
                        }
                        tsc.executeMainOperations(jssc);
                    });
                } catch (Exception e) {
                    log.error(String.format("[%s] Error on start!",
                            Constants.TW_RECEIVER_LOG_TAG), e);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    try {
                        response.getWriter().println("Failed");
                    } catch (IOException ex) {}
                }
            });
            
        } catch (IOException ex) {
            log.error(String.format("[%s] Exception during app initialization!",
                    Constants.TW_RECEIVER_LOG_TAG), ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }
}
