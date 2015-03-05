echo "Called SDA start-all.."

source confs.cfg
#####
if [ "x$SDA_HOME" == "x" ]
then
	echo "SDA_HOME variable not setted. Try to retrieve from script param.."
	if [ "x$1" == "x" ]
	then
		echo "Both SDA_HOME and <sda_home> from script param are missing. Guessing from script path..."
		SDA_HOME="$(cd "`dirname "$0"`";cd ..; pwd)"
		echo SDA_HOME is $SDA_HOME
	else
		SDA_HOME=$1
	fi
fi
export SDA_HOME

SCRIPT_DIRNAME=$(dirname "$0")
cd $SCRIPT_DIRNAME
#####
echo "loading env.."
#source start-spark-env.sh
export MASTER=spark://$SPARK_MASTER_IP:$SPARK_MASTER_PORT

while read row 
do
	MODULE_NAME=$(echo $row | cut -d " " -f1)
	MODULE_SCRIPT=$(echo $row | cut -d " " -f2)
	echo "starting $MODULE_NAME"
	source $MODULE_SCRIPT
done < modules

echo "End"