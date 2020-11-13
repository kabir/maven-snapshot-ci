#!/bin/sh# The file has been split due to its large size. Run this script to reassemble it.
WORKING_DIR=$(dirname $0)
cat ${WORKING_DIR}/xx.?? > ${WORKING_DIR}/wildfly-servlet-dist-legacy-22.0.0.Beta1-SNAPSHOT.zip
echo Reassembled ${WORKING_DIR}/wildfly-servlet-dist-legacy-22.0.0.Beta1-SNAPSHOT.zip
