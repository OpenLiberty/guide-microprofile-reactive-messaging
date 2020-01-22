FROM open-liberty

COPY src/main/liberty/config /config/
COPY target/gateway.war /config/apps

RUN configure.sh

