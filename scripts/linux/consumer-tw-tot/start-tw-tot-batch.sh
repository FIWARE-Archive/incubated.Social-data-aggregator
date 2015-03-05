function display_usage_and_exit {
  SCRIPT_NAME=$(basename $0)
  echo "$SCRIPT_NAME - submit tot-tw-consumer batch on Spark"
  echo ""
  echo "usage:"
  echo "$SCRIPT_NAME --from <from> --to <to> --roundMode <roundMode> --granMin <gran_min>"
  echo ""
  echo "--from  -> time from which you want to start the analysis (ISO8601 format)"
  echo "--to -> time to which you want to stop the analysis (ISO8601 format)"
  echo "--roundMode -> define the round mode on the creation time (min,hour,day)"
  echo "--granMin -> valid only if round mode is min.Granularity,if you want to group in minute intervals (e.g gran=5 will group by 5 minutes -> the number of tweets in 5 minutes"
  echo ""
  exit 1
}

if [ "$1" == "--help" ]
then
	display_usage_and_exit
fi

JAR_FILE_ARGS=$@
if [ "x$SDA_HOME" == "x" ]
then 
	SDA_HOME="$(cd "`dirname "$0"`";cd ../..; pwd)"
	echo SDA_HOME is $SDA_HOME	
fi

CONF_FILE=$SDA_HOME/scripts/consumer-tw-tot/consumer-tw-tot-confs.cfg
source $CONF_FILE

if [ "x$MASTER" == "x" ]
then 
	echo "Missing MASTER"
	display_usage_and_exit	
fi
if [ "x$SPARK_HOME" == "x" ]
then 
    echo "Missing SPARK_HOME"
	display_usage_and_exit	
fi

PATH_TO_JAR_FILE=$SDA_HOME/bin/consumers/batch/$JAR_FILE_NAME_BATCH

export SDA_CONF=$SDA_HOME/confs
export TOT_TW=consumers/consumer-tw-tot

echo "Submitting consumer-tw-tot batch application..."
$SPARK_HOME/bin/spark-submit --class $TOT_TW_BATCH_MAIN_CLASS --master $MASTER --deploy-mode client $PATH_TO_JAR_FILE -I $INPUT_DATA_PATH $JAR_FILE_ARGS