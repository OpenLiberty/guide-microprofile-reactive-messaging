@ECHO OFF
@ECHO building restaurantBFF image > restaurantBFF.tmp
docker build -q -t restaurantbff:1.0-SNAPSHOT restaurantBFF\.
DEL restaurantBFF.tmp