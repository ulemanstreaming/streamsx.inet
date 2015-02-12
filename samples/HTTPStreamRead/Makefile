# Copyright (C) 2011,2014, International Business Machines Corporation. 
# All Rights Reserved.

.PHONY: all standalone distributed clean 

TOOLKIT_NAME=com.ibm.streamsx.inet
STREAMS_INET_TOOLKIT ?= $(shell  ([ -e ../../$(TOOLKIT_NAME) ] && echo ../../$(TOOLKIT_NAME)) ||\
						([ -e "../com.ibm.streamsx.hbase" ] && echo ../$(TOOLKIT_NAME)) ||\
						echo $(STREAMS_INSTALL)/toolkits/$(TOOLKIT_NAME))

SPLC_FLAGS ?= -a -t $(STREAMS_INET_TOOLKIT)
SPLC = $(STREAMS_INSTALL)/bin/sc
SPL_CMD_ARGS ?= 
SPL_MAIN_COMPOSITE = com.ibm.streamsx.inet.http.sample::HTTPStreamSample

all: standalone

standalone:
	$(SPLC) $(SPLC_FLAGS) -T -M $(SPL_MAIN_COMPOSITE) $(SPL_CMD_ARGS)

distributed:
	$(SPLC) $(SPLC_FLAGS) -M $(SPL_MAIN_COMPOSITE) $(SPL_CMD_ARGS)

clean: 
	$(SPLC) $(SPLC_FLAGS) -C -M $(SPL_MAIN_COMPOSITE)
