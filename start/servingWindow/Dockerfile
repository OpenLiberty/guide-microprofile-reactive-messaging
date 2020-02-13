FROM open-liberty

COPY --chown=1001:0 src/main/liberty/config /config/
COPY --chown=1001:0 target/servingWindow.war /config/apps

EXPOSE 9082 9445

# It is recommended to run the configure.sh when build image for production.
#RUN configure.sh