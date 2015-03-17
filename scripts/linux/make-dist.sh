SDA_FOLDER_NAME="sda"
CONNECTOR_TW_FOLDER_NAME="connector-tw"
CONSUMER_TOT_TW_FOLDER_NAME="consumer-tw-tot"
CONSUMER_TOT_TW_BATCH_PATH="$CONSUMER_TOT_TW_FOLDER_NAME/consumer-tw-tot-batch"
CONSUMER_TOT_TW_STREAM_PATH="$CONSUMER_TOT_TW_FOLDER_NAME/consumer-tw-tot-stream"
SCRIPTS_FOLDER_NAME="scripts"
CONFS_FOLDER_NAME="confs"

CONNNECTORS_ROOT="producers"
CONSUMERS_ROOT="consumers"

TARGET_FOLDER="target"
CONNECTOR_BIN_REL_PATH="bin/connectors"
CONSUMERS_BIN_REL_PATH="bin/consumers"
BATCH_FOLDER="batch"
STREAM_FOLDER="stream"

function exit_with_usage {
  echo "make-dist.sh - tool for making binary distributions of SocialDataAggregator"
  echo ""
  echo "usage:"
  echo "./make-dist.sh <outputDir>"
  echo "<outputDir> output directory where to build sda"
  echo ""
  exit 1
}

if [ "x$1" == "x" ]
then
	exit_with_usage
fi

SCRIPT_NAME="$(basename $0)"
# Figure out where SDA is installed
SDA_SRC_DIR="$(cd "`dirname "$0"`"; cd ../..; pwd)"

echo "SDA src dir is $SDA_SRC_DIR"
OUTPUT_DIR=$1
OUTPUT_PATH=$OUTPUT_DIR/$SDA_FOLDER_NAME

LOG_PATH="$OUTPUT_PATH/log"

rm -rf $OUTPUT_PATH
mkdir -p $OUTPUT_PATH/$CONNECTOR_BIN_REL_PATH
mkdir -p $OUTPUT_PATH/$CONSUMERS_BIN_REL_PATH/$BATCH_FOLDER
mkdir -p $OUTPUT_PATH/$CONSUMERS_BIN_REL_PATH/$STREAM_FOLDER

#create log folder
mkdir -p $LOG_PATH/$CONNECTOR_TW_FOLDER_NAME
mkdir -p $LOG_PATH/$CONSUMER_TOT_TW_FOLDER_NAME

cp $SDA_SRC_DIR/$CONNNECTORS_ROOT/$CONNECTOR_TW_FOLDER_NAME/$TARGET_FOLDER/uber-*.jar $OUTPUT_PATH/$CONNECTOR_BIN_REL_PATH/
cp $SDA_SRC_DIR/$CONSUMERS_ROOT/$CONSUMER_TOT_TW_BATCH_PATH/$TARGET_FOLDER/uber-*.jar $OUTPUT_PATH/$CONSUMERS_BIN_REL_PATH/$BATCH_FOLDER/
cp $SDA_SRC_DIR/$CONSUMERS_ROOT/$CONSUMER_TOT_TW_STREAM_PATH/$TARGET_FOLDER/uber-*.jar $OUTPUT_PATH/$CONSUMERS_BIN_REL_PATH/$STREAM_FOLDER/

cp -r $SDA_SRC_DIR/$SCRIPTS_FOLDER_NAME/linux $OUTPUT_PATH/$SCRIPTS_FOLDER_NAME/
rm -f $OUTPUT_PATH/$SCRIPTS_FOLDER_NAME/$SCRIPT_NAME
cp -r $SDA_SRC_DIR/$CONFS_FOLDER_NAME $OUTPUT_PATH/

echo "Dist Made on path $OUTPUT_PATH"
echo "End"


