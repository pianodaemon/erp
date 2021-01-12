#!/bin/sh

PID_FILE="/tmp/sso.pid"

# Pid file is needless in container enviroment
rm -f $PID_FILE

/sso -pid-file=$PID_FILE &

/usr/sbin/nginx -g "daemon off;"
