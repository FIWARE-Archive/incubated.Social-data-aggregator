echo "Loading env configuration..."
source confs.cfg

echo "Configuring master params..."
export SPARK_MASTER_WEBUI_PORT=$SPARK_MASTER_WEBUI_PORT;
export SPARK_MASTER_IP=$SPARK_MASTER_IP;
export SPARK_MASTER_PORT=$SPARK_MASTER_PORT;

echo "Configuring workers"
export SPARK_WORKER_INSTANCES=$SPARK_WORKER_INSTANCES;

echo "Starting nodes..."
$SPARK_HOME/sbin/start-all.sh