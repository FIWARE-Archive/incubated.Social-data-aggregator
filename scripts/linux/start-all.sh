function display_usage_and_exit {
  echo "start-all.sh - "
  echo ""
  echo "usage:"
  echo "./start-all.sh --sda-home <sda_home> --start-spark-env"
  echo ""
  echo "--sda-home -> The path of social-data-aggregator folder. Optional. Provide from command line if SDA_HOME is not setted in your environment"
  echo "--start-spark-env -> launch spark in standalone mode before submit sda realtime modules"
  echo ""
  exit 1
}


echo "Called SDA start-all.."

SCRIPT_DIRNAME=$(dirname "$0")
cd $SCRIPT_DIRNAME
source confs.cfg


# Parse arguments
while (( "$#" )); do
  case $1 in
    --sda-home)
       SDA_HOME=$2
	   shift
      ;;
	--start-spark-env)
	   LOAD_ENV=1
	   shift
	  ;;
     --clear-checkpoints)
	   CLEAR_CHECKPOINTS=1
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

#####
if [ "x$LOAD_ENV" != "x" ]
then
	echo "loading spark env.."
	source start-spark-env.sh
else
	source confs.cfg #load spark configurations 
fi

if [ "x$CLEAR_CHECKPOINTS" != "x" ]
then
	echo "clearing old checkpoints.."
        rm -rf $CHECKPOINTS_DIR/*
fi

export MASTER=spark://$SPARK_MASTER_IP:$SPARK_MASTER_PORT

while read row 
do
	if [[ $row != "#"* ]]
	then
		MODULE_NAME=$(echo $row | cut -d " " -f1)
		MODULE_SCRIPT=$(echo $row | cut -d " " -f2)
		echo "starting $MODULE_NAME"
		source $MODULE_SCRIPT
	fi
done < modules

echo "End"
