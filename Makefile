ECHO = /bin/echo

BASE		:= $(shell /bin/pwd)
OPERATIONAL	:= $(BASE)/DOS

all:	reports legacy

legacy:
	(MACROS_INCLUDE=make.macros && export MACROS_INCLUDE && \
	OPERATIONAL=$(OPERATIONAL) && export OPERATIONAL && \
	cd $(OPERATIONAL)/legacy && make);

reports:
	(MACROS_INCLUDE=make.macros && export MACROS_INCLUDE && \
	OPERATIONAL=$(OPERATIONAL) && export OPERATIONAL && \
	cd $(OPERATIONAL)/reports && make);

clean:
	(MACROS_INCLUDE=make.macros && export MACROS_INCLUDE && \
	OPERATIONAL=$(OPERATIONAL) && export OPERATIONAL && \
	cd $(OPERATIONAL)/reports && make clean && \
	cd $(OPERATIONAL)/legacy && make clean);
