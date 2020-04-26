JSONFILES=$(shell (ls model_*.json))

SCHEMA=model.schema

all:: check

check: $(JSONFILES)
	@# dump stdout to /dev/null to avoid cluttering the output
	@# stuff we care about (errors and pass messages) are written to stderr
	@echo Using jsonschema to validate all files...
	@echo $^ | xargs --max-lines=1 --verbose --replace=% --delimiter=' ' jsonschema -i % $(SCHEMA) > /dev/null

