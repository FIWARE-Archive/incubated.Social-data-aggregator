echo "Called SDA start-all.."

source confs.cfg
#####
if [ "x$SDA_HOME" == "x" ]
then
	echo "SDA_HOME variable not setted. Try to retrieve from script param.."
	if [ "x$1" == "x" ]
	then
		echo "Both SDA_HOME and <sda_home> from script param are missing. start-all script cannot be executed."
		echo "Exiting.."
		exit 1
	fi
	SDA_HOME=$1
fi
export SDA_HOME
#####
echo "loading env.."
./start-spark-env.sh

echo "starting tw-connector.."
./connector-tw/start-tw-connector.sh --with-master spark://$SPARK_MASTER_IP:$SPARK_MASTER_PORT --sda-home $SDA_HOME --spark-home $SPARK_HOME

echo "End"