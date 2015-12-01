docker
======

# How to use this Dockerfile

You can build a docker image based on this Dockerfile. This image will load only Social Data Aggregator Twitter Connector. 
This requires that you have docker installed on your machine.

## Run a container from an image you just built

Once downloaded the [SDA](https://github.com/FiwareTIConsoft/social-data-aggregator) repository code simply navigate to
the docker directory and run
```
    sudo docker build -t fiware/sda .
```
This will build a new docker image and store it locally as
fiware/sda.  The option `-t fiware/sda` gives the image a name. 

To run the container from the newly created image:

```
sudo docker run -v TwConnector.properties:/TwConnector.properties fiware/sda 
```
[Docker Volumes](http://docs.docker.com/engine/userguide/dockervolumes/) allow you to map any file/directory from the host OS into a container.

In order to launch TwConnector on docker container properly you need to provide a TwConnector.properties filled with at least the following four properties:
 - twConsumerKey 
 - twConsumerSecret 
 - twToken 
 - twTokenSecret 

For all the other properties you can leave the default values.

## Access SDA on console
```
sudo docker run -v TwConnector.properties:/TwConnector.properties fiware/sda /bin/bash
```

Use this mode if u want to configure other modules or alter some default configurations.


If you want to know more about images and the building process you can find it in [Docker's documentation](https://docs.docker.com/userguide/dockerimages/).
