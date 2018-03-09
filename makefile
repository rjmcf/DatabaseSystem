SRCDIR = src
TESTDIR = test
PACKDIR = rjmdatabase

allSources :
	javac -sourcepath ${SRCDIR} ${SRCDIR}/${PACKDIR}/*/*.java

allTests : allSources
	javac -sourcepath ${SRCDIR}:${TESTDIR} ${TESTDIR}/${PACKDIR}/*/*.java

runTheTest : allSources
	FNAME=`echo ${TEST} | tr . /` ; \
	javac -sourcepath ${SRCDIR}:${TESTDIR} ${TESTDIR}/${PACKDIR}/$${FNAME}Test.java; \
	cd ${TESTDIR}; \
	java -cp ../${SRCDIR}:./ ${PACKDIR}/$${FNAME}Test

runTests : allTests
	cd ${TESTDIR}; \
	java -cp ../${SRCDIR}:./ ${PACKDIR}/testutils/TestRunner

clean :
	find . -name "*.class" -type f -delete
