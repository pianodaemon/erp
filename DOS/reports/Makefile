# The name coined to address the builder image
BIMG=reports-builder

# User and group identifiers expected
EXPUID=$(shell id -u)
EXPGID=$(shell id -g)

all: image clean
	docker run -it --rm -v$(shell pwd):/yield --user ${EXPUID}:${EXPGID} ${BIMG} /maven/apache-maven-3.6.3/bin/mvn package

clean:
	docker run -it --rm -v$(shell pwd):/yield --user ${EXPUID}:${EXPGID} ${BIMG} /maven/apache-maven-3.6.3/bin/mvn clean

image:
	docker build . -t ${BIMG}
