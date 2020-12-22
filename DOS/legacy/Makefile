include $(OPERATIONAL)/../$(MACROS_INCLUDE)

LEGACY_OUT	= $(BINDIR)/legacy

# The name coined to address the builder image
BIMG=legacy-builder

# Protobuf files for Sales API
SALES_SRV_PROTO_DIR		= $(OPERATIONAL)/sales-api/protos
SALES_LEGACY_CLI_DIR	= $(OPERATIONAL)/legacy/sales-grpc-jcli/src/main/proto

all: build copy

build: image
	@[ -d $(SALES_LEGACY_CLI_DIR) ] || (mkdir -p $(SALES_LEGACY_CLI_DIR))
	@cp -f $(SALES_SRV_PROTO_DIR)/*.proto  $(SALES_LEGACY_CLI_DIR)/
	docker run --rm -v$(shell pwd):/yield --user ${EXPUID}:${EXPGID} ${BIMG} /maven/apache-maven-3.6.3/bin/mvn package -DskipTests

copy:
	@[ -d $(LEGACY_OUT) ] || (mkdir -p $(LEGACY_OUT))
	@cp -f front-end/target/agnux.war $(LEGACY_OUT)
clean:
	- ${BLOWUP_FOLDER} $(SALES_LEGACY_CLI_DIR)
	docker run --rm -v$(shell pwd):/yield --user ${EXPUID}:${EXPGID} ${BIMG} /maven/apache-maven-3.6.3/bin/mvn clean

image:
	docker build . -t ${BIMG}
