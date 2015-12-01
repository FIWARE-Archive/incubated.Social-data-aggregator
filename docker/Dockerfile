#################################################
# Docker compose is needed  to start a cluster###
#################################################

FROM phusion/baseimage:0.9.17

# Initial update and java 8 installation
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get autoremove -y && \
    apt-get clean


# JAVA 8 installation
ENV JAVA_HOME /usr/jdk1.8.0_31
ENV PATH $PATH:$JAVA_HOME/bin
RUN curl -sL --retry 3 --insecure \
  --header "Cookie: oraclelicense=accept-securebackup-cookie;" \
  "http://download.oracle.com/otn-pub/java/jdk/8u31-b13/server-jre-8u31-linux-x64.tar.gz" \
  | gunzip \
  | tar x -C /usr/ \
  && ln -s $JAVA_HOME /usr/java \
  && rm -rf $JAVA_HOME/man

# SPARK INSTALLATION
ENV SPARK_VERSION 1.4.1
ENV HADOOP_VERSION 2.6
ENV SPARK_PACKAGE $SPARK_VERSION-bin-hadoop$HADOOP_VERSION
ENV SPARK_HOME /opt/spark-$SPARK_PACKAGE
ENV PATH $PATH:$SPARK_HOME/bin
RUN curl -sL --retry 3 \
  "http://mirrors.ibiblio.org/apache/spark/spark-$SPARK_VERSION/spark-$SPARK_PACKAGE.tgz" \
  | gunzip \
  | tar x -C /opt/ \
  && ln -s $SPARK_HOME /usr/spark

# APACHE KAFKA INSTALLATION
ENV KAFKA_VERSION 0.8.2.2
ENV SCALA_VERSION 2.10

RUN curl -sL --retry 3 \
    "https://www.apache.org/dyn/closer.cgi?path=/kafka/$KAFKA_VERSION/kafka_$SCALA_VERSION-$KAFKA_VERSION.tgz" \
  | gunzip \
  | tar x -C /opt/ \
  && ln -s kafka_$SCALA_VERSION-$KAFKA_VERSION /opt/kafka

# Retrieve sda 
ADD https://github.com/FiwareTIConsoft/social-data-aggregator/releases/download/v0.1-RELEASE/sda_v01.tar.gz /tmp

#move sda to /opt folder
ENV SDA_FOLDER_NAME sda
RUN tar -xzf /tmp/sda_v01.tar.gz /opt
RUN mv /opt/sda_v01 /opt/$SDA_FOLDER_NAME

#make folder for twconnector data
RUN mkdir /data

#start apache kafka
#RUN /opt/kafka/bin/zookeeper-server-start.sh /opt/kafka/config/zookeeper.properties 
#RUN /opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties

RUN mv /TwStreamConnector.properties /opt/sda/confs/connector-tw/TwStreamConnector.properties

# Expose ports
EXPOSE 8080 7077 8089

COPY ./startup_script.sh /tmp/startup_script.sh

#Configuring spark startup
RUN cat /opt/sda/scripts/confs.cfg.template | sed -e "s/SPARK_MASTER_IP=/SPARK_MASTER_IP=$HOSTNAME/g" > /opt/sda/scripts/confs.cfg
RUN head /opt/sda/scripts/modules > /opt/sda/scripts/modules

#Starting spark environment and TwConnector
CMD ["/opt/sda/scripts/start-spark-env.sh","/opt/sda/scripts/start-all.sh"]

