
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

Usage
-------------------

```
java -jar target/hsinflux-1.0.0-SNAPSHOT.jar 
```

[Grafana](https://grafana.com/) is a great way to produce graphs and alerts on the InfluxDB Data
