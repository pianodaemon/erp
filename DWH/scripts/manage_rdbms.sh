#!/bin/sh

CONTAINER="rdbms_staging"
export MYSQL_PWD=$MYSQL_ROOT_PASSWORD

exec mysql $MYSQL_DATABASE -u root
