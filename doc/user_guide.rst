FIWARE Social Data Aggregator - User Guide
_____________________________________________________________

SETTING UP THE START-ALL/START-ENV SCRIPTS:
==================================

This script launches all the modules of the real-time part of the social data aggregator. 

Under the **/scripts** folder there are two files:

confs.cfg.template
------------------

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

modules
------------------

This file contains all the modules that will be started from the start-all script. 
Add a comment (#) on the modules you don’t need to avoid starting them.

The script can be ran in two ways:

1. submitting the applications on an existent spark cluster:

``./start-all.sh``

2. setting  up a spark cluster in standalone mode before submitting the applications

``./start-all.sh --start-spark-env``

In both cases you need to edit the configuration file (in the first case to refer to the already existent master, in the second to know with which configurations to deploy it).

CONNECTORS
==================================

Connectors are in charge to retrieve the data from Social Networks (SNs). Each connector is “specialized”: it is connected with  a specific social network and gathers data by interacting with it. The way data are retrieved can vary from SN to SN: some SNs provide stream Apis (e.g. twitter,instagram) while others that needs to be polled by the connector. 
The connector receives live input data streams and divides the data into batches namely a specific set of data collected during a given timeframe. The content of each batch is mapped onto an internal model and then sent to the internal bus to make it available to real time consumers.
The internal stream bus is a communication bus for a loosely-coupled interconnection between modules. It can be implemented by different technologies (apache kafka, amazon kinesis, rabbitMQ..).
Data belonging to different batches are also collected in a window. The content of each window is saved on the storage as raw data in json format.  In this way raw data can be subsequently processed by batch consumers. 
The storage has to be reachable from every node of the cluster, it can be implemented by a Database (Mysql, OrientDB, MongoDB..), a distributed filesystem (HDFS..), an online file storage web service (s3) or a shared disk (NFS).

Each connector can expose apis that can be contacted from a *controller* in order to modify the settings or the topics being under monitoring. 
A topic can be based on:

* keyword(s)
* geo location (latitute,longitude..)
* a target user (if the social network allows user tracking)
* hashtags 

SETTING UP CONNECTOR-TW
------------------

CONFIGURATIONS
~~~~~~~~~~~~~~~~~~~~~~

Under the folder *sda\confs\connector-tw* you will find 3 configuration files:

**log4j.properties** 

the properties for log4j. Set where you want the connector log. Edit this file following your needs.

**twstats.cfg.xml**

configuration file for hibernate. Edit it if you compiled the GE with the DAO default implementation. If you provide a different implementation you can leave this file as is or delete it.
Edit the following fields with your database configuration:

::

    <property name="connection.url"></property>
    <property name="connection.username"> </property>
    <property name="connection.password"> </property>

You can find the model of the default DAO in social-data-aggregator/data_model in the project directory.

This is a typical paragraph.  An indented literal block follows.

**TwStreamConnector.properties**

*Twitter Configurations*

In this section of the configuration file there are all the properties regarding the connection with Twitter:

+------------------------+------------+-------------------------------------+
|  Key Name         | Optional   | Description                              | 
+===================+============+==========================================+
| twConsumerKey     | NO         | Consumer Key of the twitter application  | 
+-------------------+------------+------------------------------------------+
| twConsumerSecret  | NO         |Consumer Secret of the twitter application|         
+-------------------+------------+------------------------------------------+
| twToken           | NO         | User token                               |         
+-------------------+------------+------------------------------------------+
| twTokenSecret     | NO         | User token secret                        |         
+-------------------+------------+------------------------------------------+

*Node Configurations*

In this section of the configuration file there are the configurations regarding the node that hosts the driver:

+------------------------+------------+-------------------------------------+
|  Key Name         | Optional   | Description                              | 
+===================+============+==========================================+
| nodeName          | NO         | The name of the node (the value must be  |
|                   |            | the same of the field                    | 
|                   |            | monitoring_from_node in the db model in  |
|                   |            | case you use the default DAO). This      |  
|                   |            | property is needed in case of multiple   |
|                   |            | instances of the collector in nodes      |
|                   |            | that have different Public IPs but share |
|                   |            | the same rdbms. In this way you can      |
|                   |            | choose which key will be monitored       |
|                   |            | from a target node.                      |
+-------------------+------------+------------------------------------------+
| proxyPort         | YES        | (Uncomment this property in case you use |       
|                   |            | a proxy  for outbound connections)       |
|                   |            | The proxy port                           |
+-------------------+------------+------------------------------------------+
| proxyHost         | YES        | (Uncomment this property in case you use |
|                   |            | a proxy  for outbound connections)       |
|                   |            | The proxy host                           |
+-------------------+------------+------------------------------------------+

