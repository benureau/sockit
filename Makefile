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

