FROM open-liberty
COPY src/main/liberty/config /config/
RUN configure.sh
ADD target/job.war /config/apps
