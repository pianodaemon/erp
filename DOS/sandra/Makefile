include $(OPERATIONAL)/../$(MACROS_INCLUDE)

# Assuming we moved into sandra directory
COMPONENT_DIR	= $(shell pwd)

# Python modules required by sandra
PY_MODS		= $(COMPONENT_DIR)/requirements.txt

# Please be sure when building having internet connectivity
SCHEMA_CFDI	= http://www.sat.gob.mx/sitio_internet/cfd/4/cfdv40.xsd


all: schema

schema: setup
	pyxbgen -u ${SCHEMA_CFDI} -m v4 --module-prefix=sat

setup:
	(pip install --upgrade pip && pip install -r ${PY_MODS})
