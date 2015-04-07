package com.tilab.ca.sda.tw_user_profile_extractor.utils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

public class CommandLineArgs {

    private static final String INPUT_DATA_PATH="I";
    private static final String OUTPUT_PATH="O";
    private static final String TRAINING_DATA="T";
    
    
    private static final String HELP_OPT = "help";

    private static final Options options;

    static {
        options = new Options();
        options.addOption(new Option(HELP_OPT, "print this message"));
        options.addOption(INPUT_DATA_PATH, true, "input data path: the folder under which are stored data");
        options.addOption(OUTPUT_PATH, true, "output data path: the folder under which save data");
        options.addOption(TRAINING_DATA, false, "if the data are training data or not");     
    }

    public static Arguments parseCommandLineArgs(String[] args) throws Exception{
        CommandLineParser parser = new PosixParser();
        Arguments arguments=new Arguments();
	CommandLine cmd = parser.parse(options, args);
        
        if(cmd.hasOption(HELP_OPT)){
            printHelp();
        }
        if(cmd.hasOption(INPUT_DATA_PATH)){
            arguments.setInputDataPath(cmd.getOptionValue(INPUT_DATA_PATH));
        }
        if(cmd.hasOption(OUTPUT_PATH)){
            arguments.setOutputPath(cmd.getOptionValue(OUTPUT_PATH));
        }
        
        arguments.setAreTrainingData(cmd.hasOption(TRAINING_DATA));

        if(arguments.getInputDataPath()==null || arguments.getInputDataPath().isEmpty())
            throw new IllegalArgumentException("input data path cannot be null.");
        
        return arguments;
    }
    
    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Tw User Profile Extractor", options);
        System.exit(0);
    }
}
