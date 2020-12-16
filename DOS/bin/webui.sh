#!/bin/bash
#
# glassfish     Este script se encarga de iniciar y detener el
#               servidor de aplicaciones Java GlassFish v3.
#


prog="GlassFish v3"
asadmin="$HOME/glassfish3/bin/asadmin"

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
