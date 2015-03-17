TEMP_FILE_PATH=/tmp/tempFileSparkScript
source confs.cfg

echo "Cleaning up application.."
export SPARK_WORKER_INSTANCES=$SPARK_WORKER_INSTANCES;
echo "Stopping master and workers..."
$SPARK_HOME/sbin/stop-all.sh 
sleep 1m
echo "check if app is still running"
ps aux | grep "spark" > $TEMP_FILE_PATH
while read line
do
	pid2Kill=$(echo $line | cut -d" " -f 2)
	echo "killing pid $pid2Kill"
	kill -9 $pid2Kill
done < $TEMP_FILE_PATH

rm -f $TEMP_FILE_PATH

echo "End"