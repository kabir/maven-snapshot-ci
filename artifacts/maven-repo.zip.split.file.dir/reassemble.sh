#!/bin/sh# The file has been split due to its large size. Run this script to reassemble it.
WORKING_DIR=$(dirname $0)
cat ${WORKING_DIR}/xx.?? > ${WORKING_DIR}/maven-repo.zip
echo Reassembled ${WORKING_DIR}/maven-repo.zip
