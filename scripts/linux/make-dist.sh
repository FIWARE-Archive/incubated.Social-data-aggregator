SDA_FOLDER_NAME="sda"
CONNECTOR_TW_FOLDER_NAME="connector-tw"
SCRIPTS_FOLDER_NAME="scripts"
CONFS_FOLDER_NAME="confs"

TARGET_FOLDER="target"
CONNECTOR_BIN_REL_PATH="/bin/connectors"

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


rm -rf $OUTPUT_PATH
mkdir -p $OUTPUT_PATH/$CONNECTOR_BIN_REL_PATH/$CONNECTOR_TW_FOLDER_NAME
cp $SDA_SRC_DIR/$CONNECTOR_TW_FOLDER_NAME/$TARGET_FOLDER/uber-connector-tw-*.jar $OUTPUT_PATH/$CONNECTOR_BIN_REL_PATH/$CONNECTOR_TW_FOLDER_NAME
cp -r $SDA_SRC_DIR/$SCRIPTS_FOLDER_NAME/linux $OUTPUT_PATH/$SCRIPTS_FOLDER_NAME
rm -f $OUTPUT_PATH/$SCRIPTS_FOLDER_NAME/$SCRIPT_NAME
cp -r $SDA_SRC_DIR/$CONFS_FOLDER_NAME $OUTPUT_PATH/

echo "Dist Made on path $OUTPUT_PATH"
echo "End"


