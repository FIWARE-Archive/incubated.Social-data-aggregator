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

*Spark  Configurations*

In this section of the configuration file there are the configurations regarding the spark Streaming Context:

+--------------------------------------+------------+------------------------------------------+
|  Key Name                            | Optional   | Description                              | 
+======================================+============+==========================================+
| numMaxCore                           | YES        | Number of cores to associate to this     |
|                                      |            |  application (in case you have to run    | 
|                                      |            |  multiple streaming application)         |
|                                      |            |  If you run just the collector you can   |
|                                      |            |  comment this property                   |
+--------------------------------------+------------+------------------------------------------+
| checkpointDir                        | NO         | Directory where spark will save this     |       
|                                      |            | application  checkpoints                 |
+--------------------------------------+------------+------------------------------------------+
| sparkBatchDurationMillis             | NO         | Duration of the batch (in milliseconds). |
|                                      |            | It is the basic interval at which the    |
|                                      |            | system with receive the data in batches  |
+--------------------------------------+------------+------------------------------------------+
| sparkCleanTTL                        | NO         | Duration (seconds) of how long Spark will|
|                                      |            | remember any metadata (stages generated, |
|                                      |            | tasks generated, etc.). Periodic cleanups|
|                                      |            | will ensure that metadata older than this|
|                                      |            | duration will be forgotten.              |
+--------------------------------------+------------+------------------------------------------+
| twitterInserterWindowDuration        | NO         | Duration of the window. Both the window  |
|                                      |            | duration and the slide duration must be  |
|                                      |            | multiples of the batch interval.         |
|                                      |            | Save frequency for gathered data.        |
+--------------------------------------+------------+------------------------------------------+
| twitterInserterWindowSlidingInterval | NO         | Window sliding interval. The interval at |
|                                      |            | which the window will slide or move      |
|                                      |            | forward. (set equal to the               |
|                                      |            | twitterInserterWindowDuration to avoid   |
|                                      |            | duplicated data saved)                   |
+--------------------------------------+------------+------------------------------------------+


*App Configurations*

In this section of the configuration file there are the configurations regarding the app:

+--------------------------------------+------------+------------------------------------------+
|  Key Name                            | Optional   | Description                              | 
+======================================+============+==========================================+
| serverPort                           | NO         | The port on which jetty server will      |
|                                      |            | listen. Needed to start,restart,stop     | 
|                                      |            | the collector.                           |
+--------------------------------------+------------+------------------------------------------+
| savePartitions                       | NO         | Number of partition to coalesce before   |       
|                                      |            | save. Equals one will generate one file  |
|                                      |            | containing raw tweets for window.        |
+--------------------------------------+------------+------------------------------------------+
| dataOutputFolder                     | NO         | the folder where the raw data will be    |
|                                      |            | saved                                    |
+--------------------------------------+------------+------------------------------------------+
| dataRootFolder                       | NO         | Root folder on which data will be saved. |
|                                      |            | Example: dataOutputFolder=               |
|                                      |            | file://tmp/data and dataRootFolder=raw   |
|                                      |            | will save data on file://tmp/data/raw/...|
+--------------------------------------+------------+------------------------------------------+
| daoClass                             | YES        | class for the custom dao if you          |
|                                      |            | don't want to use the default one        |
+--------------------------------------+------------+------------------------------------------+

*Kafka Configurations*

In this section of the configuration file there are the configurations regarding the kafka. If you don’t want the data sent on kafka delete or comment the following properties:

+--------------------------------------+------------+------------------------------------------+
|  Key Name                            | Optional   | Description                              | 
+======================================+============+==========================================+
| brokersList                          | NO         | Kafka brokers list (separated by ,)      |
+--------------------------------------+------------+------------------------------------------+
| kafkaSerializationClass              | NO         |Default **kafka.serializer.StringEncoder**|       
|                                      |            | Change it if you want another serializer.|
+--------------------------------------+------------+------------------------------------------+
| kafkaRequiredAcks                    | NO         | tells Kafka the number of acks you want  |
|                                      |            | your Producer to require from the        |
|                                      |            | Broker that the message was received.    |
+--------------------------------------+------------+------------------------------------------+
| maxTotalConnections                  | NO         | number of total connections for          |
|                                      |            | the connection pool                      |
+--------------------------------------+------------+------------------------------------------+
| maxIdleConnections                   | YES        | number of idle connections for the       |
|                                      |            | connection pool                          |
+--------------------------------------+------------+------------------------------------------+
| customProducerFactoryImpl            | YES        |uncomment if needed other implementation  |
|                                      |            |for connection to bus different than kafka|
+--------------------------------------+------------+------------------------------------------+

CONSUMERS
==================================

Consumers are modules that retrieve from the storage raw data collected by the connectors or from the internal stream bus and produces different kind of analytics from gathered data.

Examples of analytics provided from the Social Data Aggregator are:
- **Basic Aggregations:** calculation of the ppm (posts per minute) or number of posts in a time range, grouped by keywords or belonging to specific geo located areas, to recognize trending topics ([consumer-tw-tot](https://github.com/FiwareTIConsoft/social-data-aggregator/wiki/Setting-up-consumer-tw-tot)).
- **Gender Recognition:** this feature is useful for social networks that don’t provide information about the gender of the user (twitter for example). Recognizing a user gender from his profile is a challenging task.
- **Sentiment Analysis:** sentiment analysis aims to determine the attitude of a commenter upon a specific topic. It is used by the SDA to infere the mood of users with respect to a monitored topic. 

By subscribing to a target topic and looking for a particular key, consumers can retrieve only the information that they really need, discarding any data when not relevant to their analytics. Result data can be saved on storage rather then re-injected to the internal bus to be processed from other consumers capable of other types of analytics. 

CONSUMER TW-TOT
------------------

**OVERVIEW**

The consumer tw tot provide a count on tweets, retweets, reply  on geo and hashtags based criteria for a user defined time interval. 

There are two versions of this module:
- Stream
- Batch

**Configuration**

The confs/consumers/consumer-tw-tot folder contains the following files:

*dao_impl.conf*

A properties file with the properties needed from the ConsumerTwTotDao implementation. If you use the **ConsumerTwTotDaoDefaultImpl** you can leave this file blank.
log4j.properties
the properties for log4j. Set where you want the connector log. Edit this file following your needs.

*twstats-tot-tw.cfg.xml*

configuration file for hibernate. Edit it if you compiled the GE with the ConsumerTwTotDao default implementation. If you provide a different implementation you can leave this file as is or delete it.

Edit the following fields with your database configuration:

::
    <property name="connection.url"></property>
    
    <property name="connection.username"> </property>
    
    <property name="connection.password"> </property>

You can find the sql code to create the consumer-tw-tot tables needed to store analytics result  in social-data-aggregator/data_model in the project directory. 

*bus_impl.conf*

This is the configuration file for the internal bus. By default is filled with apache Kafka configurations. If you want to use a different implementation please follow these steps:

1. Create a Java class that  implements the BusConnection interface
2. Set the properties you need for your implementation into the bus_impl.conf file
3. Put the path to your implementation as the value for the property busConnImplClass into the **TwTotConsumerProps.properties** file (e.g “com.mypackage.MyImplClass”)

*TwTotConsumerProps.properties:*



