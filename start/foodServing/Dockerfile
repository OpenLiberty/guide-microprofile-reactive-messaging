FROM open-liberty

COPY src/main/liberty/config /config/
RUN configure.sh
ADD target/inventory.war /config/apps
