ECHO = /bin/echo

BASE		:= $(shell /bin/pwd)
OPERATIONAL	:= $(BASE)/DOS

all:	sandra legacy

legacy:
	(MACROS_INCLUDE=make.macros && export MACROS_INCLUDE && \
	OPERATIONAL=$(OPERATIONAL) && export OPERATIONAL && \
	cd $(OPERATIONAL)/legacy && make);

sandra:
	(MACROS_INCLUDE=make.macros && export MACROS_INCLUDE && \
	OPERATIONAL=$(OPERATIONAL) && export OPERATIONAL && \
	cd $(OPERATIONAL)/sandra && python3 -mvenv . && \
        . ./bin/activate && make);

clean:
	(MACROS_INCLUDE=make.macros && export MACROS_INCLUDE && \
	OPERATIONAL=$(OPERATIONAL) && export OPERATIONAL && \
	cd $(OPERATIONAL)/sandra && make clean && \
	cd $(OPERATIONAL)/legacy && make clean);
