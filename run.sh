java -jar \
-Djava.rmi.server.hostname=192.168.75.71 \
-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.port=9010 \
-Dcom.sun.management.jmxremote.local.only=false \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.ssl=false \
target/hsinflux-1.0.0-SNAPSHOT.jar

