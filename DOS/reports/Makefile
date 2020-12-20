include $(OPERATIONAL)/../$(MACROS_INCLUDE)

REPORTS_OUT	= $(BINDIR)/reports

# The name coined to address the builder image
BIMG=reports-builder

# User and group identifiers expected
EXPUID=$(shell id -u)
EXPGID=$(shell id -g)

all: build copy

build: image
	docker run --rm -v$(shell pwd):/yield --user ${EXPUID}:${EXPGID} ${BIMG} /maven/apache-maven-3.6.3/bin/mvn package

copy:
	@[ -d $(REPORTS_OUT) ] || (mkdir -p $(REPORTS_OUT))
	@cp -f cli/target/reports-cli-MAZINGER.jar $(REPORTS_OUT)

clean:
	docker run --rm -v$(shell pwd):/yield --user ${EXPUID}:${EXPGID} ${BIMG} /maven/apache-maven-3.6.3/bin/mvn clean

image:
	docker build . -t ${BIMG}
