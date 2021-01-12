package main

import (
	"flag"
	"fmt"
	"io/ioutil"
	"os"
	"strconv"
	"syscall"

	"github.com/sirupsen/logrus"

	platform "immortalcrab.com/sso/pkg/service"
)

const name = "Slack-SSO"

var pidFile string
var logger *logrus.Logger

func writePidFile() error {
	if piddata, err := ioutil.ReadFile(pidFile); err == nil {
		if pid, err := strconv.Atoi(string(piddata)); err == nil {
			if process, err := os.FindProcess(pid); err == nil {
				/* If sig is 0, then no signal is sent,
				   but error checking is still performed.
				   Trick applied to check for the existence
				   of a process ID or process group ID. */
				if err := process.Signal(syscall.Signal(0)); err == nil {
					return fmt.Errorf("pid already running: %d", pid)
				}
			}
		}
	}
	/* If we get here, then the pidfile didn't exist,
	   or the pid in it doesn't belong to the user running this app.*/
	return ioutil.WriteFile(pidFile, []byte(fmt.Sprintf("%d", syscall.Getpid())), 0664)
}

func main() {

	defaultPidFile := fmt.Sprintf("/run/user/%d/sso.pid", syscall.Getuid())

	flag.StringVar(&pidFile, "pid-file", defaultPidFile, "The pathname of the process ID file.")

	flag.Parse()

	/* Write a pid file, but first make sure
	   it doesn't exist with a running pid. */
	if err := writePidFile(); err != nil {
		panic(err)
	}

	logger = logrus.New()

	logger.Out = os.Stdout
	logger.Formatter = &logrus.JSONFormatter{}
	logger.Level = logrus.InfoLevel

	if err := platform.Engage(logger); err != nil {
		logger.Fatalf("%s service struggles with (%v)", name, err)
	}

	if err := syscall.Unlink(pidFile); err != nil {
		panic(err)
	}

	syscall.Exit(0)
}
