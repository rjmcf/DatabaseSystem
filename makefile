allSources :
	javac -sourcepath src src/rjmdatabase/*/*.java

allTests :
	javac -sourcepath src:test test/rjmdatabase/*/*.java

theTest :
	javac -sourcepath src:test test/rjmdatabase/${FILE}Test.java
	cd test; \
	java -cp ../src:./ rjmdatabase/${FILE}Test

runTests : allSources allTests
	cd test; \
	java -cp ../src:./ rjmdatabase/testutils/TestRunner

clean :
	find . -name "*.class" -type f -delete
