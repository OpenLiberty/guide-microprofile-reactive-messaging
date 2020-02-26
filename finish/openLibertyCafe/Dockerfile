FROM open-liberty

LABEL \
  org.opencontainers.image.authors="My Name" \
  org.opencontainers.image.vendor="Open Liberty" \
  org.opencontainers.image.url="local" \
  org.opencontainers.image.source="https://github.com/OpenLiberty/guide-microprofile-reactive-messaging" \
  org.opencontainers.image.version="$VERSION" \
  org.opencontainers.image.revision="$REVISION" \
  vendor="Open Liberty" \
  name="openLibertyCafe" \
  version="$VERSION-$REVISION" \
  summary="The openLibertyCafe microservice from the MicroProfile Reactive Messaging guide" \
  description="This image contains the openLibertyCafe microservice running with the Open Liberty runtime."

COPY --chown=1001:0 src/main/liberty/config /config/
COPY --chown=1001:0 target/openLibertyCafe.war /config/apps

# It is recommended to run the configure.sh when build image for production.
# RUN configure.sh