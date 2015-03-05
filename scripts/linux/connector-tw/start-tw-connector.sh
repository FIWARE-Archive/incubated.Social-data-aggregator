echo "submitting tw-connector app on spark.."

function display_usage_and_exit {
  echo "start-tw-connector.sh - submit tw-connector on Spark"
  echo ""
  echo "usage:"
  echo "./start-tw-connector.sh --sda-home <sda_home> --with-master <master> --spark-home <spark_home>"
  echo ""
  echo "--sda-home -> The path of social-data-aggregator folder. Optional. Provide from command line if SDA_HOME is not setted in your environment"
  echo "--with-master -> master name (eg local,spark://xxx.xxx). Optional. Provide from command line if MASTER is not setted in your environment"
  echo "--spark-home -> The path of spark folder. Optional. Provide from command line if SPARK_HOME is not setted in your environment"
  echo ""
  exit 1
}

# Parse arguments
while (( "$#" )); do
  case $1 in
    --sda-home)
       SDA_HOME=$2
	   shift
      ;;
    --with-master)
       MASTER=$2
       shift
	  ;;
    --spark-home)
      SPARK_HOME=$2
	  shift
      ;;
    --help)
      display_usage_and_exit
      ;;
    *)
      break
      ;;
  esac
  shift
done

if [ "x$SDA_HOME" == "x" ]
then 
	echo "Missing SDA_HOME from params. Guessing it from script path.."
	SDA_HOME="$(cd "`dirname "$0"`";cd ../..; pwd)"
	echo SDA_HOME is $SDA_HOME	
fi

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

CONF_FILE=$SDA_HOME/scripts/connector-tw/tw-connector-confs.cfg

source $CONF_FILE

PATH_TO_JAR_FILE=$SDA_HOME/bin/connectors/connector-tw/$JAR_FILE_NAME

SDA_CONF=$SDA_HOME/confs/connector-tw

echo "Submitting tw-connector application..."
nohup $SPARK_HOME/bin/spark-submit --class $TW_CONN_MAIN_CLASS --master $MASTER --deploy-mode client $PATH_TO_JAR_FILE $SDA_CONF &