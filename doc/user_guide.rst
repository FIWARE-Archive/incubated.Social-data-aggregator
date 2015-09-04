FIWARE Social Data Aggregator - User Guide
_____________________________________________________________

SETTING UP THE START-ALL/START-ENV SCRIPTS:
==================================

This script launches all the modules of the real-time part of the social data aggregator. 

Under the **/scripts** folder there are two files:

1.confs.cfg.template
~~~~~~~~~~~~~~~~~~~~~~

Remove the *.template* extension and edit the file providing:

+-------------------------+----------------------------------------+
| Property                |    Description                         |
+=========================+========================================+
| SPARK_HOME              | the location of your spark installation|
+-------------------------+----------------------------------------+
| SPARK_MASTER_WEBUI_PORT | port for the web ui                    |
+-------------------------+----------------------------------------+
|SPARK_MASTER_IP          | IP of spark master                     |
+-------------------------+----------------------------------------+
| SPARK_MASTER_PORT       | spark master port                      |
+-------------------------+----------------------------------------+
| SPARK_WORKER_INSTANCES  | number of worker instance per node     |
|                         | (default 3)                            |
+-------------------------+----------------------------------------+
| SDA_HOME                | (optional) Home of SDA (otherwise will |
|                         | be guessed by the script from the      |
|                         | location of the start-all script)      |
+-------------------------+----------------------------------------+

2.modules
~~~~~~~~~~~~~~~~~~~~~~

This file contains all the modules that will be started from the start-all script. 
Add a comment (#) on the modules you don’t need to avoid starting them.

The script can be ran in two ways:
1. submitting the applications on an existent spark cluster:

``./start-all.sh``

2. setting  up a spark cluster in standalone mode before submitting the applications

``./start-all.sh --start-spark-env``

In both cases you need to edit the configuration file (in the first case to refer to the already existent master, in the second to know with which configurations to deploy it).


