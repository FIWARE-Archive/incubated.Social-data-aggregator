package com.tilab.ca.sda.sda_controller.services;

import com.tilab.ca.sda.sda_controller.utils.CommandUtils;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@Service
public class DashboardService {
    
    private static final Logger  log = LoggerFactory.getLogger(DashboardService.class);
    
    @Autowired
    private ConfigurationService configurationService;
    
    
    private static final String BASH_COMMAND = "bash ";
    private static final String SCRIPT_PATH = "scripts/";
    private static final String START_COMMAND = "start-all.sh";
    private static final String START_WITH_ENV_OPT = " --start-spark-env";
    private static final String STOP_COMMAND = "stop-spark-env.sh";
    
    private static final String TAILF_COMMAND = "tail -f ";
    private static final String STARTUP_LOG_NAME = "nohup.out";
    
    
    
    public void startSda(boolean startEnv) throws Exception{
        //String sdaHomePath = getSdaHomePath();
        String command = BASH_COMMAND+getSdaHomePath()+SCRIPT_PATH+START_COMMAND;
        command = startEnv?command+START_WITH_ENV_OPT:command;
        //String command = "ls -la";
        executeCommand(command,(outputLine)-> log.info(outputLine));
    }
    
    public void stopSda() throws Exception{
        String command = BASH_COMMAND+getSdaHomePath()+SCRIPT_PATH+STOP_COMMAND;
        executeCommand(command,(outputLine)-> log.info(outputLine));
    }
    
    public void getSdaStartupLogContent(ResponseBodyEmitter emitter) throws Exception{
        String command = TAILF_COMMAND+STARTUP_LOG_NAME;
        CommandUtils.executeCommandOnShell(command, (outputLine) ->{
            log.info(outputLine);
            emitter.send(outputLine);
        });
        emitter.complete();
    }
    
    private void executeCommand(String command,CommandUtils.CommandOutputConsumer consumer) throws Exception{
        log.info("executing command {} on sda..",command);
        log.info("command output is:");
        CommandUtils.executeCommandOnShell(command, consumer);
        log.info("command executed");
    }
    
    
    
    private String getSdaHomePath(){
        String sdaHomePath = configurationService.getGlobalConfs().getSdaHome().getValue();
        return sdaHomePath.endsWith(File.separator)?sdaHomePath:sdaHomePath+File.separator;
    }
}
