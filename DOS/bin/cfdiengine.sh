#!/bin/bash

CFDIENGINE_HOME=$HOME/cfdiengine
STARTUP_SCRIPT='run.py -d'
START_OUT=$CFDIENGINE_HOME/resources/logs/start.out

start() {
    cd $CFDIENGINE_HOME
    nohup ./$STARTUP_SCRIPT > $START_OUT 2>&1 &
}

stop() {
    PATTERN="run.py"
    for pid in $( ps -ef | awk -v pattern="$PATTERN" '/pattern/ {print $2}' | head -n -1 )
    do
        echo $pid
        kill -9 $pid
    done
    pkill -f $PATTERN
}

restart() {
    stop
    sleep 5
    start
}

case "$1" in
  start)
        start
        ;;
  stop)
        # It is stoped twice as a workaround
        stop
        ;;
  restart)
        restart
        ;;
  *)
        echo $"Usage: $0 {start|stop|restart}"
        exit 2
esac

exit $?
