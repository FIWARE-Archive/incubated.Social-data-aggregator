#!/bin/bash
echo "Configuring spark startup..."
cat /opt/sda/scripts/confs.cfg.template | sed -e "s/SPARK_MASTER_IP=/SPARK_MASTER_IP=$HOSTNAME/g" > /opt/sda/scripts/confs.cfg
echo "Starting spark environment.."
/opt/sda/scripts/start-spark-env.sh
echo "SDA installation done. Please ssh into the container and configure SDA before starting the component"
