
HomeSeer InfluxDB Importer
========

A Java application which imports HomeSeer device status data into InfluxDB


License
-------------------

hsinflux is distributed under the BSD 3-Clause License.

Usage
-------------------

```
java -jar target/hsinflux-1.0.0-SNAPSHOT.jar --hsurl http://192.168.75.129/JSON --hsuser hs --hspassword hs --influxurl=http://192.168.75.71:8086 --influxuser=hs --influxpassword=hs
```