all: sockit.jar

clean:
	rm -f *.class */*.class */*/*.class */*/*/*.class */*/*/*/*.class *.pyc */*.pyc */*/*.pyc */*/*/*.pyc; \
	rm -Rf build/*

library: src/sockit/Server.java src/sockit/InboundMessage.java src/sockit/OutboundMessage.java src/sockit/Client.java
	mkdir -p build; \
	rm -Rf build/sockit/; \
	javac src/sockit/*.java -d build/

sockit.jar: library
	mkdir -p build; \
	cd build; \
	jar -cf sockit.jar sockit/

example: sockit.jar
	javac src/ExampleDataStream.java -cp build/sockit.jar -d build/; \
	cd build; java ExampleDataStream

test: sockit.jar
	javac src/Test.java -cp build/ -d build/; \
	cd build; java Test

fibserver:
	javac pysockit/sockit/test/FibServer.java -cp build/ -d build/; \
	cd build; java FibServer
