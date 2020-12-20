ECHO = /bin/echo

BASE		:= $(shell /bin/pwd)
OPERATIONAL	:= $(BASE)/DOS

all:	legacy

legacy:
	(MACROS_INCLUDE=make.macros && export MACROS_INCLUDE && \
	OPERATIONAL=$(OPERATIONAL) && export OPERATIONAL && \
	cd $(OPERATIONAL)/legacy && make);

clean:
	(MACROS_INCLUDE=make.macros && export MACROS_INCLUDE && \
	OPERATIONAL=$(OPERATIONAL) && export OPERATIONAL && \
	cd $(OPERATIONAL)/legacy && make clean);
