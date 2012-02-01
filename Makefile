
clean:
	rm -f *.class */*.class */*/*.class */*/*/*.class */*/*/*/*.class *.pyc */*.pyc */*/*.pyc */*/*/*.pyc;
	rm -Rf build/*
	
library: src/sockit/Server.java src/sockit/Message.java src/sockit/Client.java src/sockit/utils/Utils.java
	rm -Rf build/sockit/
	javac src/sockit/*.java src/sockit/*/*.java -d build/

sockit.jar: library
	jar -cf build/sockit.jar build/sockit/

Example: sockit.jar
	javac src/Example.java -cp buid/sockit.jar -d build/
	java build/Example
	
playjar: src/sockit/
	ls
	
FibServer:
	ls