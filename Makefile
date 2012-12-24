
clean:
	rm -f *.class */*.class */*/*.class */*/*/*.class */*/*/*/*.class *.pyc */*.pyc */*/*.pyc */*/*/*.pyc; \
	rm -Rf build/*

library: src/sockit/Server.java src/sockit/InputMessage.java src/sockit/OutputMessage.java src/sockit/Client.java
	rm -Rf build/sockit/; \
	javac src/sockit/*.java -d build/

sockit.jar: library
	cd build; \
	jar -cf sockit.jar sockit/

Example: sockit.jar
	javac src/ExampleDataStream.java -cp build/sockit.jar -d build/; \
	cd build; java ExampleDataStream

Test: sockit.jar
	javac src/Test.java -cp build/ -d build/; \
	cd build; java Test

FibServer:
	javac pysockit/sockit/test/FibServer.java -cp build/ -d build/; \
	cd build; java FibServer
