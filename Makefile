ECHO = /bin/echo

BASE		:= $(shell /bin/pwd)
OPERATIONAL	:= $(BASE)/DOS

all:	cfdi-api legacy

legacy:
	(MACROS_INCLUDE=make.macros && export MACROS_INCLUDE && \
	OPERATIONAL=$(OPERATIONAL) && export OPERATIONAL && \
	cd $(OPERATIONAL)/legacy && make);

cfdi-api:
	(MACROS_INCLUDE=make.macros && export MACROS_INCLUDE && \
	OPERATIONAL=$(OPERATIONAL) && export OPERATIONAL && \
	cd $(OPERATIONAL)/cfdi-api && python3 -mvenv . && \
        . ./bin/activate && make);

clean:
	(MACROS_INCLUDE=make.macros && export MACROS_INCLUDE && \
	OPERATIONAL=$(OPERATIONAL) && export OPERATIONAL && \
	cd $(OPERATIONAL)/cfdi-api && make clean && \
	cd $(OPERATIONAL)/legacy && make clean);
