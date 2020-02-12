FROM open-liberty

COPY --chown=1001:0 src/main/liberty/config /config/
COPY --chown=1001:0 target/kitchen.war /config/apps

EXPOSE 9083 9446

# It is recommended to run the configure.sh when build image for production.
# RUN configure.sh