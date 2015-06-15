package com.tilab.ca.sda.consumer.tw.tot.batch.utils;

import com.tilab.ca.sda.ctw.utils.RoundType;
import com.tilab.ca.sda.ctw.utils.Utils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

public class CommandLineArgs {

    private static final String FROM = "from";
    private static final String TO = "to";
    private static final String INPUT_DATA_PATH="I";
    private static final String ROUND_MODE="roundMode";
    private static final String GRAN_MIN="granMin";
    
    private static final String HELP_OPT = "help";

    private static final Options options;

    static {
        options = new Options();
        options.addOption(new Option(HELP_OPT, "print this message"));
        options.addOption(FROM, true, "time from which you want to start the analysis (ISO8601 format)");
        options.addOption(TO, true, "time to which you want to stop the analysis (ISO8601 format)");
        options.addOption(INPUT_DATA_PATH, true, "input data path: the folder under which are stored data");
        options.addOption(ROUND_MODE, true, "define the round mode on the creation time (min,hour,day)");
        options.addOption(GRAN_MIN, true, "valid only if round mode is min.Granularity,if you want to group in minute intervals (e.g"
                + " gran=5 will group by 5 minutes -> the number of tweets in 5 minutes");
        
    }

    public static Arguments parseCommandLineArgs(String[] args) throws Exception{
        CommandLineParser parser = new PosixParser();
        Arguments arguments=new Arguments();
	CommandLine cmd = parser.parse(options, args);
        if(cmd.hasOption(HELP_OPT)){
            printHelp();
        }
        if(cmd.hasOption(FROM)){
            arguments.setFrom(Utils.Time.fromShortTimeZoneString2ZonedDateTime(cmd.getOptionValue(FROM)));
        }    
        if(cmd.hasOption(TO)){
            arguments.setTo(Utils.Time.fromShortTimeZoneString2ZonedDateTime(cmd.getOptionValue(TO)));
        }
        if(cmd.hasOption(INPUT_DATA_PATH)){
            arguments.setInputDataPath(cmd.getOptionValue(INPUT_DATA_PATH));
        }
        if(cmd.hasOption(ROUND_MODE)){
            arguments.setRoundMode(RoundType.fromString(cmd.getOptionValue(ROUND_MODE)));
        }
        if(cmd.hasOption(GRAN_MIN)){
           arguments.setGranMin(Integer.parseInt(cmd.getOptionValue(GRAN_MIN)));
        }
      
        if(arguments.getFrom()==null || arguments.getTo()==null){
            throw new IllegalArgumentException("params from and to are mandatory!");
        }
        if(arguments.getGranMin()!=null && arguments.getRoundMode()!=RoundType.ROUND_TYPE_MIN)
            throw new IllegalArgumentException("gran min param cannot be valorized without roundType min");
        
        return arguments;
    }
    
    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Consumer Tw tot batch", options);
        System.exit(0);
    }
}
