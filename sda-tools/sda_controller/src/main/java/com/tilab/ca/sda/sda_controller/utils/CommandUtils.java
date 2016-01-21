package com.tilab.ca.sda.sda_controller.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class CommandUtils {
    
    public static void executeCommandOnShell(String command, CommandOutputConsumer outputConsumer) throws Exception {
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String outputLine;
        StringBuilder sb = new StringBuilder();
        sb.append("******************************************\n");
        sb.append("Error stream:\n");
        while ((outputLine = errorReader.readLine()) != null) {
            sb.append(outputLine+"\n");
        }
        sb.append("Output stream is\n");
        while ((outputLine = reader.readLine()) != null) {
            sb.append(outputLine+"\n");
        }
        sb.append("******************************************\n");
        outputConsumer.consume(sb.toString());
    }
    
    public static interface CommandOutputConsumer {

        void consume(String outputLine) throws Exception;
    }
}
