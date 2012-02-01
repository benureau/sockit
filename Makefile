
clean:
	rm -f *.class */*.class */*/*.class */*/*/*.class */*/*/*/*.class *.pyc */*.pyc */*/*.pyc */*/*/*.pyc;
	rm -Rf build/*
	
library: src/sockit/Server.java src/sockit/Message.java src/sockit/Client.java src/sockit/utils/Utils.java
	rm -Rf build/sockit/
	javac src/sockit/*.java src/sockit/*/*.java -d build/
	
playjar: src/sockit/
	ls
	
FibServer:
	ls