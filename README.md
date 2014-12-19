[![][TelecomLogo]][website]

Copyright © 2014 Telecom Italia.


What is Social Data Aggregator
---------------
Social Data Aggregator (SDA) GE retrieve data from different Social Networks and provide different analytics (real time and batch) 
depending on user needs. The GE relies on Apache Spark for computation on data.

To deploy Social Data Aggregator you have to do the following steps:
* clone the project from GitHub:
```
git clone https://github.com/FiwareTIConsoft/social-data-aggregator.git
```

* In social-data-aggregator/connector-tw folder there are 3 folders:
 * dev
 * test
 * prod
 
 and a **config.properties.template** file. This file has to be copied on the folder that corresponds to the profile you want to use for the build. All the fields have to be filled (only the ones in which is specified can be left blank). To avoid that the system won't work properly don't comment or delete any property. 
* build the project with the choosen profile: 
```
mvn clean package -P test
```

* start the applications:
 * Fill all the requested properties in the configuration files under scripts/linux/confs.cfg and scripts/linux/connector-tw/tw-connector-confs.cfg 
 * start single components:
    * Environment:  start-spark-env.sh, stop-spark-env.sh
    * connector-tw : start-tw-connector.sh
 * All the components:
    *  start-all.sh

Source
------
The source code of this project can be cloned from the [GitHub Repository].
Code for related libs can be found on [GitHub FiwareTIConsoft Group].



[TelecomLogo]: http://www.telecomitalia.it/sites/all/themes/pti_bo/img/logo.png
[GitHub Repository]: https://github.com/FiwareTIConsoft/social-data-aggregator
[GitHub FiwareTIConsoft Group]: https://github.com/FiwareTIConsoft
[website]: http://www.telecomitalia.it