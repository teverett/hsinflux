
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/049ad3bb9199453b8670e1afbeaa9b2e)](https://www.codacy.com/manual/teverett/hsinflux?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=teverett/hsinflux&amp;utm_campaign=Badge_Grade)

HomeSeer InfluxDB Importer
========

A Java application which imports HomeSeer device status data into InfluxDB


License
-------------------

hsinflux is distributed under the BSD 3-Clause License.

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

[Grafana](https://grafana.com/) is a great way to produce graphs and alerts on the InfluxDB Data
