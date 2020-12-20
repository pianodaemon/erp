include $(OPERATIONAL)/../$(MACROS_INCLUDE)

LEGACY_OUT	= $(BINDIR)/legacy

# The name coined to address the builder image
BIMG=legacy-builder

# User and group identifiers expected
EXPUID=$(shell id -u)
EXPGID=$(shell id -g)

all: build copy

build: image
	docker run -it --rm -v$(shell pwd):/yield --user ${EXPUID}:${EXPGID} ${BIMG} /maven/apache-maven-3.6.3/bin/mvn package -DskipTests

copy:
	@[ -d $(LEGACY_OUT) ] || (mkdir -p $(LEGACY_OUT))
	@cp -f front-end/target/agnux.war $(LEGACY_OUT)
clean:
	docker run --rm -v$(shell pwd):/yield --user ${EXPUID}:${EXPGID} ${BIMG} /maven/apache-maven-3.6.3/bin/mvn clean

image:
	docker build . -t ${BIMG}
