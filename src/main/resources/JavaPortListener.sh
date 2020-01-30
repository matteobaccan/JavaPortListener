#! /bin/bash
# Start JavaPortListener

# Java bin path
JAVA=/usr/bin/java

# AbsolutePath of JavaPortListener
PORTLISTENER=/root/portlistener/

# Java parameter
COMANDO=" -jar JavaPortListener-1.0.1-jar-with-dependencies.jar"


case "$1" in
    start)
	check=`pgrep -f -- "$COMANDO"`
	if [ "$check" = "" ]; then
		cd $PORTLISTENER
		echo -n "Start JavaPortListaner..."
		echo
		# JavaPortListaner in background...
		$JAVA $COMANDO&
	else
		echo -n "JavaPostListener is running"
		echo
	fi
	;;
    stop)
	echo -n "Close JavaPortListener"
	echo
	## Kill JavaPortListener with killall on process
	pid=`pgrep -f -- "$COMANDO"`
	kill -9 $pid
	;;
    status)
	echo -n "Check JavaPortListener state: "
	echo
	## Check the state of JavaPortListener
	ps -p 1 `pgrep -f -- "$COMANDO"` |
		 awk '{ORS="" ; if ($3 ~ /^[TWXZ]/) {print "1"} else print "0"}' |
		 awk '{if ($1 > 0) {print "The process in inactive or there are some problem with the process"} else {print "The process is running"} }'
	;;
    restart)
	echo -n "Close JavaPortListener"
	echo
	## Kill JavaPortListener with killall on process
	pid=`pgrep -f -- "$COMANDO"`
        kill -9 $pid

	cd $PORTLISTENER
	echo -n "JavaPortListener restart.."
	echo
	# JavaPortListaner in background...
	$JAVA $COMANDO&
	;;
    *)
	echo "Syntax: $0 {start|stop|status|restart}"
	exit 1
	;;
esac


