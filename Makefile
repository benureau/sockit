all: sockit.jar

clean:
	rm -f *.class */*.class */*/*.class */*/*/*.class */*/*/*/*.class *.pyc */*.pyc */*/*.pyc */*/*/*.pyc; \
	rm -Rf target/*

library: src/sockit/Server.java src/sockit/InboundMessage.java src/sockit/OutboundMessage.java src/sockit/Client.java
	mkdir -p target; \
	rm -Rf target/sockit/; \
	javac -Xlint:unchecked src/sockit/*.java -d target/

sockit.jar: library
	mkdir -p target; \
	cd target; \
	jar -cf sockit.jar sockit/

example: sockit.jar
	javac src/ExampleDataStream.java -cp target/sockit.jar -d target/; \
	cd target; java ExampleDataStream

test: sockit.jar
	javac src/Test.java -cp target/ -d target/; \
	cd target; java Test

fibserver:
	javac pysockit/sockit/test/FibServer.java -cp target/ -d target/; \
	cd target; java FibServer
