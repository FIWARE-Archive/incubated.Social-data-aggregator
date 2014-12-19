echo "submitting tw-connector app on spark.."
source $1/tw-connector-confs.cfg


echo "Submitting tw-connector application..."
nohup $SPARK_HOME/bin/spark-submit --class $TW_CONN_MAIN_CLASS --master $MASTER --deploy-mode client $PATH_TO_JAR_FILE &