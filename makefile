SRCDIR = src
TESTDIR = test
PACKDIR = rjmdatabase
DBDIR = databases
DBS = $(patsubst $(DBDIR)/%,%,$(wildcard $(DBDIR)/*))
GREP_COLORS='mt=1;41;37'

.SILENT:

## Display this help text
help:
	$(info Available targets)
	@awk '/^[a-zA-Z\-\_0-9]+:/ {                    \
	  nb = sub( /^## /, "", helpMsg );              \
	  if(nb == 0) {                                 \
	    helpMsg = $$0;                              \
	    nb = sub( /^[^:]*:.* ## /, "", helpMsg );   \
	  }                                             \
	  if (nb)                                       \
	    print  $$1 "\t" helpMsg;                    \
	}                                               \
	{ helpMsg = $$0 }'                              \
	$(MAKEFILE_LIST) | column -ts $$'\t' |          \
	grep --color '^[^ ]*'

## Compiles the sourcefiles.
allSources:
	javac -sourcepath ${SRCDIR} ${SRCDIR}/${PACKDIR}/*/*.java

## Compiles the test files.
allTests: allSources
	javac -sourcepath ${SRCDIR}:${TESTDIR} ${TESTDIR}/${PACKDIR}/*/*.java

## Runs a particular test specified with TEST="<package>.<class>".
runTheTest: allSources
	FNAME=`echo ${TEST} | tr . /` ; \
	javac -sourcepath ${SRCDIR}:${TESTDIR} ${TESTDIR}/${PACKDIR}/$${FNAME}Test.java; \
	cd ${TESTDIR}; \
	java -cp ../${SRCDIR}:./ ${PACKDIR}/$${FNAME}Test

## Runs all the tests.
runTests: allTests
ifdef DISABLE_INTERACTIVITY
	cd ${TESTDIR}; \
	java -cp ../${SRCDIR}:./ ${PACKDIR}/testutils/TestRunner 1
else
	cd ${TESTDIR}; \
	java -cp ../${SRCDIR}:./ ${PACKDIR}/testutils/TestRunner
endif

## Start the textual interface, with name of database equal to NAME argument.
textInterface: allSources
	java -cp ${SRCDIR} ${PACKDIR}/userinterface/TextInterface ${DBDIR}/${NAME}


## Removes all the .class files.
clean:
	find . -name "*.class" -type f -delete

## Lists the names of databases available
listDBs:
	@$(foreach db,${DBS},echo ${db};)
