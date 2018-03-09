SRCDIR = src
TESTDIR = test
PACKDIR = rjmdatabase
GREP_COLORS='mt=1;41;37'

.SILENT:
.PHONY: help

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
	cd ${TESTDIR}; \
	java -cp ../${SRCDIR}:./ ${PACKDIR}/testutils/TestRunner

## Removes all the .class files.
clean:
	find . -name "*.class" -type f -delete

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
