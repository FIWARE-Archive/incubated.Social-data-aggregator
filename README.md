[![][TelecomLogo]][website]

Copyright © 2014 Telecom Italia.


What is Social Data Aggregator
---------------
Social Data Aggregator (SDA) GE retrieves data from different Social Networks and provides different analytics (real time and batch) 
depending on user needs. The GE relies on Apache Spark for computation on data.

To deploy Social Data Aggregator you have to do the following steps:
* clone the project from GitHub:
```
git clone https://github.com/FiwareTIConsoft/social-data-aggregator.git
```

* build the project: 
```
mvn clean package
```
Once built SocialDataAggregator with Maven, under the folder **scripts/your_os_env/** there is a script called make-dist. Run it with the following syntax (e.g. linux):
```
./make-dist.sh <output_folder>
```
where output_folder is the folder inside which you want to create the SDA GE folder tree. 
When the script finish to run, you should see the following dir tree:
```
sda
 |
 | -  bin (contains all the binaries of sda in their respective folder)
 |
 | - confs (contains the configurations of each specific sub-component)
 |
 | - scripts (contains the launch scripts for each sub-component and a start-all script to start all components)
 ```
 
##Setting up the start-all script
This script can be used if you don't have an instance of spark already running. With this script Spark will run in standalone mode.
Under the **/scripts** folder there is a file called **confs.cfg.template**. Remove the **.template** extension and edit the file providing:
 ```
SPARK_HOME -> the location of your spark installation
SPARK_MASTER_WEBUI_PORT -> if you want a different port for the webui edit this param otherwise leave 8080  
SPARK_MASTER_IP -> ip of spark master 
SPARK_MASTER_PORT -> spark master port
SPARK_WORKER_INSTANCES -> number of worker instances: default 3
SDA_HOME -> home of social-data-aggregator GE.
 ```
 
 
##Setting up connector-tw

* **Script:**  *sda\scripts\connector-tw* -> **tw-connector-confs.cfg.template**: edit the file removing the extension **.template**. If the default settings are fine for your installation leave them as is, otherwise modify them following your needs.

* **Confs:** under the folder *sda\confs\connector-tw* you will find 3 configuration files:
 * **log4j.properties:** the properties for log4j. Set where you want the connector log. Edit this file following your needs.
 * **twstats.cfg.xml:** configuration file for hibernate. Edit it if you compiled the GE with the DAO default implementation. If you provide a different implementation you can leave this file as is or delete it.
 * **TwStreamConnector.properties:** properties of the component. If you want to use a custom DAO put the name (as well the package) of your custom implementation class (e.g com.mypackage.otherstuff.MyDaoImpl).
 
Source
------
The source code of this project can be cloned from the [GitHub Repository].
Code for related libs can be found on [GitHub FiwareTIConsoft Group].



[TelecomLogo]: http://www.telecomitalia.it/sites/all/themes/pti_bo/img/logo.png
[GitHub Repository]: https://github.com/FiwareTIConsoft/social-data-aggregator
[GitHub FiwareTIConsoft Group]: https://github.com/FiwareTIConsoft
[website]: http://www.telecomitalia.it