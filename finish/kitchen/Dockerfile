FROM open-liberty

COPY src/main/liberty/config /config/
COPY target/kitchen.war /config/apps

RUN configure.sh