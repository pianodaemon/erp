#!/bin/bash
#
# glassfish     Este script se encarga de iniciar y detener el
#               servidor de aplicaciones Java GlassFish v3.
#

export JAVA_HOME="$ERP_ROOT/jdk-legacy"
export PATH="$JAVA_HOME/bin:$PATH"

export MS_DBMS_URL="jdbc:postgresql://${MS_DBMS_HOST}:${MS_DBMS_PORT}/${MS_DBMS_DB}"

asadmin="$ERP_ROOT/glassfish3/bin/asadmin"

start() {
        $asadmin start-domain
}
stop() {
        $asadmin stop-domain
}
restart() {
        $asadmin restart-domain
}

case "$1" in
  start)
        start
        ;;
  stop)
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
