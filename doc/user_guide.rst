FIWARE Social Data Aggregator - User Guide
_____________________________________________________________

SETTING UP THE START-ALL/START-ENV SCRIPTS:
==================================

This script launches all the modules of the real-time part of the social data aggregator. 

Under the **/scripts** folder there are two files:

*1.confs.cfg.template*. 

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


