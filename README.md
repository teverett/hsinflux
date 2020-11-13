![CI](https://github.com/teverett/hsinflux/workflows/CI/badge.svg)

HomeSeer InfluxDB Importer
========

A Java application which imports HomeSeer device status data into InfluxDB

hsInflux is one of numerous HomeSeer support applications created by khubla.com, including

* [hsClient](https://github.com/teverett/hsclient)
* [hsInflux](https://github.com/teverett/hsinflux)
* [hsMQTT](https://github.com/teverett/hsOpenAPI)
* [hsOpenAPI](https://github.com/teverett/hsOpenAPI)


License
-------------------

hsinflux is distributed under the BSD 3-Clause License.

Screen shots
-------------------

[Grafana](https://grafana.com/) was used to produce graphs and alerts on the InfluxDB Data

House Temperatures

![temperatures.png](https://github.com/teverett/hsinflux/blob/master/screenshots/temperatures.png)

House Relative Humidity

![humidities.png](https://github.com/teverett/hsinflux/blob/master/screenshots/humidities.png)

Battery Status

![batteries.png](https://github.com/teverett/hsinflux/blob/master/screenshots/batteries.png)

Dimmer Status

![dimmers.png](https://github.com/teverett/hsinflux/blob/master/screenshots/dimmers.png)


Configuration
-------------------

hsinflux is configured via the file "hsinflux.properties".  A typical example is

```
# HomeSeer
hsurl=http://192.168.75.129/JSON
hsuser=HOMESEERUSERNAME
hspassword=HOMESEERPASSWORD

# Influx
influxurl=http://192.168.75.71:8086
influxuser=INFLUXUSERNAME
influxpassword=INFLUXPASSWORD
influxdb=hsinflux

# Number of threads to poll HomeSeer on
pollingthreads=5

# minutes
pollinginterval=1

```
Preparing the InfluxDB database
-------------------

The following command create the InfluxDB database

```
CREATE DATABASE hsinflux 
CREATE RETENTION POLICY hsinflux ON hsinflux DURATION 52w REPLICATION 1
USE hsinflux
CREATE USER INFLUXUSERNAME WITH PASSWORD 'INFLUXPASSWORD' WITH ALL PRIVILEGES;
```

Usage
-------------------

```
java -jar target/hsinflux-1.0.0-SNAPSHOT.jar 
```
A log file is generated to hsinflux.log and rotated every 30 days.

