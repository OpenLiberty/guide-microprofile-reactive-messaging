FROM open-liberty:kernel

COPY --chown=1001:0 src/main/liberty/config /config/
COPY --chown=1001:0 target/order.war /config/apps

EXPOSE 9081 9444

# It is recommended to run the configure.sh when build image for production.
# RUN configure.sh